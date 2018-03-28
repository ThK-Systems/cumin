/*
 * tksCommons
 *
 * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 *
 */

package de.thksystems.util.concurrent.scalingworkerqueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thksystems.util.concurrent.Consumers;
import de.thksystems.util.concurrent.ThreadUtils;

public class ScalingWorkerQueue<E, C extends WorkerQueueConfiguration> {

    private enum Status {
        CREATED, START_TRIGGERED, STARTED, STOP_TRIGGERED, STOPPED;
    }

    public static final long WAIT_FOR_STATUS_PERIOD = 10L;

    private static final Logger LOG = LoggerFactory.getLogger(ScalingWorkerQueue.class);

    private Status status = Status.CREATED;

    private final Function<Integer, Collection<E>> supplier;
    private final BiConsumer<E, C> worker;
    private final C configuration;

    private ThreadFactory threadFactory = new BasicThreadFactory.Builder()
            .uncaughtExceptionHandler((thread, throwable) -> LOG.error("Uncaught error in thread '{}': {}", thread, throwable.getMessage(), throwable))
            .build();

    private Function<Thread, String> dispatcherThreadNameSupplier = Thread::getName;
    private BiFunction<Thread, Integer, String> workerThreadNameSupplier = (thread, numberOfRunner) -> thread.getName();

    private Function<E, Boolean> trylockFunction = element -> true;
    private Consumer<E> unlockFunction = Consumers.noOp();
    private Function<E, Boolean> integrityCheckFunction = element -> true;

    private Queue<E> internalQueue = new ConcurrentLinkedQueue<>();
    private Set<E> elementsInWork = ConcurrentHashMap.newKeySet();
    private List<Runner> runners = new ArrayList<>();

    public ScalingWorkerQueue(C configuration, Function<Integer, Collection<E>> supplier, BiConsumer<E, C> worker) {
        this.configuration = configuration;
        this.supplier = supplier;
        this.worker = worker;
    }

    public ScalingWorkerQueue<E, C> withThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ScalingWorkerQueue<E, C> withDistributedSetup(Function<E, Boolean> trylockFunction, Consumer<E> unlockFunction, Function<E, Boolean> integrityCheckFunction) {
        if(trylockFunction != null) {
            this.trylockFunction = trylockFunction;
        }
        if(unlockFunction != null) {
            this.unlockFunction = unlockFunction;
        }
        if(integrityCheckFunction != null) {
            this.integrityCheckFunction = integrityCheckFunction;
        }
        return this;
    }

    public ScalingWorkerQueue<E, C> withThreadNames(Function<Thread, String> dispatcherThreadNameSupplier, BiFunction<Thread, Integer, String> workerThreadNameSupplier) {
        this.dispatcherThreadNameSupplier = dispatcherThreadNameSupplier;
        this.workerThreadNameSupplier = workerThreadNameSupplier;
        return this;
    }

    public ScalingWorkerQueue<E, C> start() {
        return start(false);
    }

    public ScalingWorkerQueue<E, C> start(boolean waitForStart) {
        status = Status.START_TRIGGERED;
        threadFactory.newThread(this::run).start();
        while (waitForStart && !isStarted()) {
            ThreadUtils.sleepWithoutException(WAIT_FOR_STATUS_PERIOD);
        }
        return this;
    }

    public boolean isStarted() {
        return status == Status.STARTED;
    }

    public ScalingWorkerQueue stop() {
        return stop(false);
    }

    public ScalingWorkerQueue stop(boolean waitForStop) {
        LOG.info("Requesting stop of worker queue");
        status = Status.STOP_TRIGGERED;
        while (waitForStop && !isStopped()) {
            ThreadUtils.sleepWithoutException(WAIT_FOR_STATUS_PERIOD);
        }
        return this;
    }

    public boolean shouldStop() {
        return status == Status.STOP_TRIGGERED;
    }

    public boolean isStopped() {
        return status == Status.STOPPED;
    }

    private void run() {
        String oldThreadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(dispatcherThreadNameSupplier.apply(Thread.currentThread()));
            LOG.info("Worker queue started");
            status = Status.STARTED;

            int elementsPerRunner = configuration.getCountOfElementsPerRunner();
            int maxRunner = configuration.getMaxRunnerCount();
            int minRunner = configuration.getMinRunnerCount();
            int spareElementCount = configuration.getSpareElementsCountToSupply();

            Long idleWaitUntil, addedCount;

            while (!shouldStop()) {
                try {
                    // Get elements by supplying function
                    LOG.trace("Fetching additional elements");
                    Collection<E> elements = supplier.apply(Math.max(configuration.getMinElementsCountToSupply(), elementsPerRunner * runners.size() + spareElementCount));
                    LOG.debug("Fetched {} additional elements", elements.size());
                    idleWaitUntil = null;
                    addedCount = 0L;

                    // Add only fetched elements, that are not already in the queue
                    // This does not break concurrency, because this is the only place and thread, elements are added to the queue.
                    // (This does not have a good performance for larger queues, but we should not have them.)
                    for (E element : elements) {
                        if(!internalQueue.contains(element) && !elementsInWork.contains(element)) {
                            LOG.trace("Adding fetched element to internal queue: {}", element);
                            internalQueue.add(element);
                            addedCount++;
                        } else {
                            LOG.trace("Skipping fetched element. It is already in the internal queue or currently processed: {}", element);
                        }
                    }
                    // If no (new) elements are added to the internal queue, we sleep some time ...
                    if(addedCount == 0) {
                        LOG.trace("No (new) element fetched. Waiting some time.");
                        idleWaitUntil = System.currentTimeMillis() + configuration.getDispatcherWaitPeriodOnEmptyFetch();
                    }

                    // Create runner/worker threads, if needed
                    while (!shouldStop() && runners.size() < Math.min(maxRunner, (double) elements.size() / elementsPerRunner)) {
                        Runner runner = new Runner(runners.size(), runners.size() > minRunner);
                        runners.add(runner);
                        threadFactory.newThread(runner).start();
                    }

                    // Wait until the size of the internal queue falls below a given limit.
                    while (!shouldStop()
                            && ((internalQueue.size() >= spareElementCount && idleWaitUntil == null)
                            || (idleWaitUntil != null && System.currentTimeMillis() < idleWaitUntil))) {
                        try {
                            Thread.sleep(configuration.getSleepPeriod());
                        } catch (InterruptedException e) {
                            throw new UnsupportedOperationException("The scaling worker queue must not interrupted. Use stop() instead.", e);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Caught exception: {} -> Sleeping some time", e.getMessage(), e);
                    int i = 10;
                    while (!shouldStop() && i-- >= 0) {
                        ThreadUtils.sleepWithoutException(configuration.getSleepPeriod());
                    }
                }
            }
        } finally {
            while (getRunnersCount() > 0) { // Wait for runners to be stopped (to not kill their 'parent' thread)
                ThreadUtils.sleepWithoutException(WAIT_FOR_STATUS_PERIOD);
            }
            status = Status.STOPPED;
            LOG.info("Worker queue stopped");
            Thread.currentThread().setName(oldThreadName);
        }
    }

    synchronized Optional<E> getNextElement() {
        E element = internalQueue.peek();
        if(element != null) {
            elementsInWork.add(element);
            internalQueue.remove();
        }
        return Optional.ofNullable(element);
    }

    boolean hasNextElement() {
        return !internalQueue.isEmpty();
    }

    void markElementAsProcessed(E element) {
        if(element != null) {
            elementsInWork.remove(element);
        }
    }

    void removeRunner(Runner runner) {
        runners.remove(runner);
    }

    public int getRunnersCount() {
        return runners.size();
    }

    private class Runner implements Runnable {
        private final int number;
        private final boolean canDieIfIdle;

        private Long noResultStartTime = null;

        Runner(int number, boolean canDieIfIdle) {
            this.number = number;
            this.canDieIfIdle = canDieIfIdle;
        }

        @Override
        public void run() {
            String oldThreadName = Thread.currentThread().getName();
            try {
                Thread.currentThread().setName(workerThreadNameSupplier.apply(Thread.currentThread(), number));
                LOG.info("Additional runner started: {} (mandatory: {})", number, !canDieIfIdle);

                while (!ScalingWorkerQueue.this.shouldStop()) {
                    Optional<E> optionalElement = ScalingWorkerQueue.this.getNextElement();
                    // Process element
                    if(optionalElement.isPresent()) {
                        E element = optionalElement.get();
                        LOG.info("Got: {}", element);
                        try {
                            noResultStartTime = null;   // Reset idle counter (in case of no result)
                            if(trylockFunction.apply(element) && integrityCheckFunction.apply(element)) {
                                try {
                                    worker.accept(element, configuration);
                                } catch (Throwable throwable) {
                                    LOG.error(throwable.getMessage(), throwable);
                                } finally {
                                    unlockFunction.accept(element);
                                }
                            }
                        } finally {
                            markElementAsProcessed(element);
                        }
                    }
                    // No result
                    else {
                        LOG.trace("Got no element");
                        if(noResultStartTime == null) {
                            noResultStartTime = System.currentTimeMillis();  // Remember timestamp of first no result
                        }
                        // Check, if this runner (thread) should (and can) die, because it is idle too long
                        else if(canDieIfIdle && System.currentTimeMillis() > noResultStartTime + configuration.getRunnerMaxIdlePeriod()) {
                            LOG.info("Runner is idle and will be stopped.");
                            break;
                        }
                        // Wait ...
                        Long idleWaitUntil = System.currentTimeMillis() + configuration.getRunnerSleepIdlePeriod();
                        while (!ScalingWorkerQueue.this.shouldStop() && System.currentTimeMillis() < idleWaitUntil) {
                            try {
                                Thread.sleep(configuration.getSleepPeriod());
                            } catch (InterruptedException e) {
                                throw new UnsupportedOperationException("The runner thread must not interrupted.", e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error("Caught exception {}", e.getMessage(), e);
            } finally {
                LOG.info("Runner {} stopped", number);
                Thread.currentThread().setName(oldThreadName);
                ScalingWorkerQueue.this.removeRunner(this);
            }
        }

    }

}