package org.teamtators.rotator.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Stepper implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Stepper.class);
    private static final double DEFAULT_PERIOD = 1.0 / 120.0;
    private static final double S_TO_NS = 1000000000.0;

    private ScheduledExecutorService executorService;
    private double period;
    private ITimeProvider timeProvider = new SystemNanoTimeTimeProvider();

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    private SortedSet<Steppable> steppables = new ConcurrentSkipListSet<>(new SteppableExecutionOrderComparator());
    private boolean running = false;
    private double lastStepTime;

    public Stepper() {
        this(Executors.newSingleThreadScheduledExecutor());
    }

    @Inject
    public Stepper(ScheduledExecutorService executorService) {
        this(executorService, DEFAULT_PERIOD);
    }

    public Stepper(ScheduledExecutorService executorService, double period) {
        this.executorService = executorService;
        this.period = period;
    }

    public Stepper(double period) {
        this(Executors.newSingleThreadScheduledExecutor(), period);
    }

    public void start() {
        if (isRunning()) return;
        writeLock.lock();
        try {
            running = true;
            lastStepTime = timeProvider.getTimestamp();
            executorService.scheduleAtFixedRate(this, 0, (long) (S_TO_NS * this.period), TimeUnit.NANOSECONDS);
        } finally {
            writeLock.unlock();
        }
    }

    public void stop() {
        writeLock.lock();
        try {
            running = false;
            executorService.shutdown();
        } finally {
            writeLock.unlock();
        }
    }

    public boolean isRunning() {
        readLock.lock();
        try {
            return running;
        } finally {
            readLock.unlock();
        }
    }

    public void add(Steppable steppable) {
        writeLock.lock();
        try {
            steppable.onEnable();
            steppables.add(steppable);
        } finally {
            writeLock.unlock();
        }
    }

    public void remove(Steppable steppable) {
        writeLock.lock();
        try {
            steppables.remove(steppable);
            steppable.onDisable();
        } finally {
            writeLock.unlock();
        }
    }

    public boolean contains(Steppable steppable) {
        return steppables.contains(steppable);
    }

    @Override
    public void run() {
        readLock.lock();
        try {
            double time = timeProvider.getTimestamp();
            double delta = time - lastStepTime;
            lastStepTime = time;
            for (Steppable steppable : steppables) {
                try {
                    steppable.step(delta);
                } catch (Throwable t) {
                    logger.error("Exception in steppable step method", t);
                }
            }
        } finally {
            readLock.unlock();
        }
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    @Inject
    public void setTimeProvider(ITimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

}
