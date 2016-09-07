package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for the Vision Client
 * Receives vision data from the Raspberry Pi via NetworkTables
 */
public abstract class AbstractVision extends Subsystem {

    public AbstractVision() {super("Vision");}

    /**
     * Sets targeting LED brightness
     *
     * @param power LED brightness
     */
    public abstract void setLEDPower(double power);

    /**
     * Resets LED brightness to zero
     */
    public void resetLEDPower() {
        setLEDPower(0d);
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
