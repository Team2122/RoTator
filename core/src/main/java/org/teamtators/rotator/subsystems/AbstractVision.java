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
     * Gets the current vision data from the vision system.
     *
     * @return The current vision data.
     */
    public abstract VisionData getVisionData();

    public abstract void setTurretAngle(double turretAngle);
}
