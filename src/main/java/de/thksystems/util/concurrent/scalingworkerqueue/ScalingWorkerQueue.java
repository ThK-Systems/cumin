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

public class ScalingWorkerQueue<E, C extends WorkerQueueConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(ScalingWorkerQueue.class);

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

    private boolean stop = false;

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
        threadFactory.newThread(this::run).start();
        return this;
    }

    public ScalingWorkerQueue stop() {
        LOG.info("Stopping worker queue");
        this.stop = true;
        return this;
    }

    public boolean shouldStop() {
        return stop;
    }

    private void run() {
        String oldThreadName = Thread.currentThread().getName();
        LOG.info("Worker queue started");
        try {
            Thread.currentThread().setName(dispatcherThreadNameSupplier.apply(Thread.currentThread()));

            int elementsPerRunner = configuration.getCountOfElementsPerRunner();
            int maxRunner = configuration.getMaxRunnerCount();
            int minRunner = configuration.getMinRunnerCount();

            Long idleWaitUntil;

            while (!shouldStop()) {

                // Get elements by supplying function
                LOG.debug("Fetching additional elements");
                int spareElementCount = configuration.getSpareElementsCountToSupply();
                Collection<E> elements = supplier.apply(Math.max(configuration.getMinElementsCountToSupply(), elementsPerRunner * runners.size() + spareElementCount));
                LOG.debug("Fetched {} elements", elements.size());
                idleWaitUntil = null;
                if(elements.isEmpty()) {
                    idleWaitUntil = System.currentTimeMillis() + configuration.getDispatcherWaitPeriodOnEmptyFetch();
                } else {
                    // Add only fetched elements, that are not already in the queue
                    // This does not break concurrency, because this is the only place and thread, elements are added to the queue.
                    // (This does not have a good performance for larger queues, but we should not have them.)
                    for (E element : elements) {
                        if(!internalQueue.contains(element) && !elementsInWork.contains(element)) {
                            LOG.trace("Adding fetched element to internal queue: {}", element);
                            internalQueue.add(element);
                        } else {
                            LOG.trace("Skipping fetched element. It is already in the internal queue or currently processed: {}", element);
                        }
                    }
                }

                // Create runner/worker threads, if needed
                while (!shouldStop() && runners.size() < Math.min(maxRunner, elements.size() / elementsPerRunner)) {
                    Runner runner = new Runner(runners.size(), runners.size() > minRunner);
                    runners.add(runner);
                    threadFactory.newThread(runner).start();
                }

                // Wait until the size of the internal queue falls below a given limit.
                while (!shouldStop()
                        && internalQueue.size() >= spareElementCount
                        && (idleWaitUntil == null || System.currentTimeMillis() < idleWaitUntil)) {
                    try {
                        Thread.sleep(configuration.getDispatcherSleepPeriod());
                    } catch (InterruptedException e) {
                        throw new UnsupportedOperationException("The scaling worker queue must not interrupted. Use stop() instead.", e);
                    }
                }
            }
        } finally {
            LOG.info("Worker queue stopped");
            Thread.currentThread().setName(oldThreadName);
        }
    }

    private synchronized Optional<E> getNextElement() {
        E element = internalQueue.peek();
        if(element != null) {
            elementsInWork.add(element);
            internalQueue.remove();
        }
        return Optional.ofNullable(element);
    }

    private void markElementAsProcessed(E element) {
        if(element != null) {
            elementsInWork.remove(element);
        }
    }

    private void removeRunner(Runner runner) {
        runners.remove(runner);
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
            LOG.debug("Additional runner started: {} (mandatory: {})", number, !canDieIfIdle);
            String oldThreadName = Thread.currentThread().getName();
            try {
                Thread.currentThread().setName(workerThreadNameSupplier.apply(Thread.currentThread(), number));

                while (!ScalingWorkerQueue.this.shouldStop()) {
                    Optional<E> optionalElement = ScalingWorkerQueue.this.getNextElement();
                    LOG.debug("Getting and processing element: {}", optionalElement);
                    // Process element
                    if(optionalElement.isPresent()) {
                        E element = optionalElement.get();
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
                        if(noResultStartTime == null) {
                            noResultStartTime = System.currentTimeMillis();  // Remember timestamp of first no result
                        }
                        // Check, if this runner (thread) should (and can) die, because it is idle too long
                        else if(canDieIfIdle && System.currentTimeMillis() > noResultStartTime + configuration.getRunnerMaxIdlePeriod()) {
                            LOG.debug("Runner is idle and will be stopped.");
                            ScalingWorkerQueue.this.removeRunner(this);
                            break;
                        }
                        // Wait ...
                        try {
                            Thread.sleep(configuration.getRunnerSleepIdlePeriod());
                        } catch (InterruptedException e) {
                            throw new UnsupportedOperationException("The runner thread must not interrupted.", e);
                        }
                    }
                }
                LOG.debug("Runner {} stopped", number);
            } finally {
                Thread.currentThread().setName(oldThreadName);
            }
        }

    }

}