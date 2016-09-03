package org.teamtators.rotator.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Stepper implements Runnable {
    public static final int DEFAULT_PERIOD = 10;
    private List<Steppable> toStep = new ArrayList<>();
    private int period;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ITimeProvider timeProvider = new SystemNanoTimeTimeProvider();
    private boolean running = false;
    private Thread thread;

    public Stepper() {
        this(DEFAULT_PERIOD);
    }

    public Stepper(int period) {
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
        if(!isEnabled(steppable)) {
            toStep.add(steppable);
        }
    }

    public void remove(Steppable steppable) {
        toStep.remove(steppable);
    }

    @Override
    public void run() {
        long lastStepped = timeProvider.currentTimeMillis();
        while (running) {
            long nextStepped = timeProvider.currentTimeMillis();
            long delta = nextStepped - lastStepped;
            for (Steppable steppable : toStep) {
                steppable.step(delta / 1000.0);
            }
            long delay = period - delta;
            if (delay < 0) {
                logger.warn("Stepping took " + (-delay) + " milliseconds longer than configured period");
            } else {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
            }
            lastStepped = nextStepped;
        }
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Inject
    public void setTimeProvider(ITimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public boolean isEnabled(Steppable steppable) {
        return toStep.contains(steppable);
    }
}
