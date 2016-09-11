package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.components.Gyro;
import org.teamtators.rotator.components.SimulationGyro;
import org.teamtators.rotator.subsystems.AbstractDrive;
import org.teamtators.rotator.subsystems.DriveMode;

/**
 * drive implementation which does nothing
 */
public class NoopDrive extends AbstractDrive {

    private SimulationGyro simulationGyro = new SimulationGyro();

    @Override
    public void setLeftPower(double leftPower) {

    }

    @Override
    public void setRightPower(double rightPower) {

    }

    @Override
    public double getLeftRate() {
        return 0;
    }

    @Override
    public double getRightRate() {
        return 0;
    }

    @Override
    public double getLeftDistance() {
        return 0;
    }

    @Override
    public double getRightDistance() {
        return 0;
    }

    @Override
    public void resetEncoders() {

    }

    @Override
    public void setDriveMode(DriveMode driveMode) {

    }

    @Override
    public Gyro getGyro() {
        return simulationGyro;
    }
}
