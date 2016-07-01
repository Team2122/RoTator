package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.subsystems.DriveMode;
import org.teamtators.rotator.subsystems.IDrive;

/**
 * drive implementation which does nothing
 */
public class NoopDrive implements IDrive{
    @Override
    public void setPowers(float leftPower, float rightPower) {

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
