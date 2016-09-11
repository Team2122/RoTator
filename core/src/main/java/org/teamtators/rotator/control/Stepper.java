package org.teamtators.rotator.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class Stepper implements Runnable {
    public static final double DEFAULT_PERIOD = 1.0 / 120.0;
    private Set<Steppable> steppables = new HashSet<>();
    private double period;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ITimeProvider timeProvider = new SystemNanoTimeTimeProvider();
    private boolean running = false;
    private Thread thread;

    public Stepper() {
        this(DEFAULT_PERIOD);
    }

    public Stepper(double period) {
        this.period = period;
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }

    public void add(Steppable steppable) {
        steppables.add(steppable);
    }

    public void remove(Steppable steppable) {
        steppables.remove(steppable);
    }

    public boolean contains(Steppable steppable) {
        return steppables.contains(steppable);
    }

    @Override
    public void run() {
        double lastStepTime = timeProvider.getTimestamp();
        while (running) {
            double stepTime = timeProvider.getTimestamp();
            double delta = stepTime - lastStepTime;
            for (Steppable steppable : steppables) {
                steppable.step(delta);
            }
            long delay = (long) ((period - delta) * 1000);
            if (delay < 0) {
                logger.warn("Stepping took " + (-delay) + " milliseconds longer than configured period");
            } else {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
            }
            lastStepTime = stepTime;
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
