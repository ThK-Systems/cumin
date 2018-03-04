/*
 *
 *  * tksCommons
 *  *
 *  * Author  : Thomas Kuhlmann (ThK-Systems, https://www.thk-systems.de)
 *  * License : LGPL (https://www.gnu.org/licenses/lgpl.html)
 *
 */

package de.thksystems.util.concurrent.scalingworkerqueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.thksystems.util.concurrent.LockerI;

public class ScalingWorkerQueue<E> {

    private static final Logger LOG = LoggerFactory.getLogger(ScalingWorkerQueue.class);
    // TODO - More logging

    private final Function<Integer, Collection<E>> supplier;
    private final BiConsumer<E, QueueConfiguration> worker;
    private final Queue<E> internalQueue = new ConcurrentLinkedQueue<>();
    private final List<Runner> runners = new ArrayList<>();
    private LockerI<E> locker;
    private QueueConfiguration configuration = new DefaultQueueConfiguration();
    private ThreadFactory threadFactory = new BasicThreadFactory.Builder()
            .uncaughtExceptionHandler((thread, throwable) -> LOG.error("Uncaught error in thread '{}': {}", thread, throwable.getMessage(), throwable))
            .build();
    private boolean stop = false;

    public ScalingWorkerQueue(Function<Integer, Collection<E>> supplier, BiConsumer<E, QueueConfiguration> worker) {
        // TODO - threadname for dispatcher and runner (as supplier/function)
        this.supplier = supplier;
        this.worker = worker;
    }

    public ScalingWorkerQueue<E> withThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ScalingWorkerQueue<E> withLocker(LockerI<E> locker) {
        this.locker = locker;
        return this;
    }

    public ScalingWorkerQueue<E> withConfiguration(QueueConfiguration queueConfiguration) {
        this.configuration = queueConfiguration;
        return this;
    }

    public void start() {
        threadFactory.newThread(this::run);
    }

    public void stop() {
        this.stop = true;
    }

    public boolean shouldStop() {
        return stop;
    }

    private void run() {
        int elementsPerRunner = configuration.getElementsPerRunner();
        int maxRunner = configuration.getMaxRunner();
        int minRunner = configuration.getMinRunner();

        while (shouldStop()) {

            // Get elements by supplying function
            Collection<E> elements = supplier.apply(Math.max(configuration.getMinElementsToSupply(), elementsPerRunner * runners.size() + configuration.getSupplySpareElementCount()));
            internalQueue.addAll(elements);

            // Create runner/worker threads, if needed
            while (shouldStop() && runners.size() < Math.min(maxRunner, elementsPerRunner * runners.size())) {
                Runner runner = new Runner<>(this, runners.size(), runners.size() > minRunner, worker, locker, configuration);
                runners.add(runner);
                threadFactory.newThread(runner).start();
            }

            // TODO: Immediately
            // Wait until the size of the internal queue falls below a given limit.
            while (shouldStop() && internalQueue.size() > elementsPerRunner * runners.size()) {
                try {
                    Thread.sleep(configuration.getDispatcherWaitPeriod());
                } catch (InterruptedException e) {
                    throw new UnsupportedOperationException("The scaling worker queue must not interrupted. Use stop() instead.", e);
                }
            }
        }
    }

    private Optional<E> getNextElement() {
        // TODO - Get next, if there is one (do not fetch any)
        return Optional.empty();
    }

    private void removeRunner(Runner<E> runner) {
        runners.remove(runner);
    }

    private static class Runner<E> implements Runnable {
        private final ScalingWorkerQueue<E> dispatcher;
        private final int number;
        private final boolean canDieIfIdle;
        private final BiConsumer<E, QueueConfiguration> worker;
        private final LockerI<E> locker;
        private final QueueConfiguration configuration;

        private Long noResultStartTime = null;

        Runner(ScalingWorkerQueue<E> dispatcher, int number, boolean canDieIfIdle, BiConsumer<E, QueueConfiguration> worker, LockerI<E> locker, QueueConfiguration configuration) {
            this.dispatcher = dispatcher;
            this.number = number;
            this.canDieIfIdle = canDieIfIdle;
            this.worker = worker;
            this.locker = locker;
            this.configuration = configuration;
        }

        @Override
        public void run() {
            while (dispatcher.shouldStop()) {
                Optional<E> element = dispatcher.getNextElement();
                // Process element
                if(element.isPresent()) {
                    noResultStartTime = null;   // Reset
                    if(locker.tryLock(element.get())) {
                        try {
                            worker.accept(element.get(), configuration);
                        } catch (Throwable throwable) {
                            LOG.error(throwable.getMessage(), throwable);
                        } finally {
                            locker.unlock(element.get());
                        }
                    }
                }
                // No result
                else {
                    if(noResultStartTime == null) {
                        noResultStartTime = System.currentTimeMillis();  // Remember timestamp of first no result
                    }
                    // Check, if this runner (thread) should (and can) die, because it is idle for some time
                    else if(canDieIfIdle && System.currentTimeMillis() > noResultStartTime + configuration.getRunnerMaxWaitIdle()) {
                        dispatcher.removeRunner(this);
                        break;
                    }
                    // Wait ...
                    try {
                        Thread.sleep(configuration.getRunnerWaitIdle());
                    } catch (InterruptedException e) {
                        throw new UnsupportedOperationException("The runner thread must not interrupted.", e);
                    }
                }
            }
        }
    }

}
