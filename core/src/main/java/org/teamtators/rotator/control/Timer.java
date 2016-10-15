package org.teamtators.rotator.control;

import javax.inject.Inject;

/**
 * Basic timer
 */
public class Timer {
    private double startTime;
    private ITimeProvider timeProvider;

    @Inject
    public Timer(ITimeProvider timeProvider) {
        startTime = Double.NEGATIVE_INFINITY;
        this.timeProvider = timeProvider;
    }

    /**
     * Initialize the timer
     */
    public void start() {
        startTime = timeProvider.getTimestamp();
    }

    /**
     * Reset the timer and get current time
     *
     * @return Current time
     */
    public double restart() {
        double time = get();
        start();
        return time;
    }

    /**
     * Get the elapsed time
     *
     * @return Elapsed time since start
     */
    public double get() {
        return timeProvider.getTimestamp() - startTime;
    }

    /**
     * Set start time to a value such that any timeout will finish
     */
    public void reset() {
        startTime = Double.NEGATIVE_INFINITY;
    }

    /**
     * Check if a period has passed, and reset timer if it has
     *
     * @return Whether or not the period has passed
     */
    public boolean hasPeriodElapsed(double period) {
        boolean hasPassed = false;
        if (get() > period) {
            hasPassed = true;
            start();
        }
        return hasPassed;
    }
}
