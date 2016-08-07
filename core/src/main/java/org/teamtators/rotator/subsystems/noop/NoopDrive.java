package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.subsystems.AbstractDrive;
import org.teamtators.rotator.subsystems.DriveMode;

/**
 * drive implementation which does nothing
 */
public class NoopDrive extends AbstractDrive {
    @Override
    public void setLeftPower(float leftPower) {

    }

    @Override
    public void setRightPower(float rightPower) {

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
}
