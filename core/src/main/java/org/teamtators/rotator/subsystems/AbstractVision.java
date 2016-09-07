package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for the Vision Client
 * Receives vision data from the Raspberry Pi via NetworkTables
 */
public abstract class AbstractVision extends Subsystem{

    public AbstractVision() {super("Vision");}

    public abstract void setLEDPower(double power);

    public void resetLEDPower() {setLEDPower(0f);}

    public abstract double getDistance();

    public abstract double getAngle();
}
