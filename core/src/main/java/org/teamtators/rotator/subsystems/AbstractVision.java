package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for the Vision Client
 * Receives vision data from the Raspberry Pi via NetworkTables
 */
public abstract class AbstractVision extends Subsystem {

    public AbstractVision() {super("Vision");}

    private boolean ledState = false;

    /**
     * Sets whether the vision leds are on or off
     * @param on If true, turns the leds on. If false, turns the leds off
     */
    public void setLedState(boolean on) {
        ledState = on;
    }

    /**
     * Gets the current led state
     * @return True if the leds are on
     */
    public boolean getLedState() {
        return ledState;
    }

    /**
     * Gets the most recently updated distance from the target from the Raspberry pi
     *
     * @return distance from the target
     */
    public abstract double getDistance();

    /**
     * Gets the most recently updated relative turret angle from the target from the Raspberry pi
     * This is an offset from the current turret angle, not the absolute angle
     *
     * @return target relative turret angle
     */
    public abstract double getAngle();
}
