package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.IGyro;
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

    @Override
    public IGyro getGyro() {
        return new IGyro() {
            @Override
            public void setCalibrationPeriod(double calibrationPeriod) {

            }

            @Override
            public double getCalibrationPeriod() {
                return 0;
            }

            @Override
            public void fullReset() {

            }

            @Override
            public void startCalibration() {

            }

            @Override
            public void finishCalibration() {

            }

            @Override
            public double getCalibrationOffset() {
                return 0;
            }

            @Override
            public boolean isCalibrating() {
                return false;
            }

            @Override
            public double getRate() {
                return 0;
            }

            @Override
            public double getAngle() {
                return 0;
            }

            @Override
            public void resetAngle() {

            }
        };
    }

    @Override
    public double getGyroAngle() {
        return 0;
    }

    @Override
    public void resetGyroAngle() {

    }

    @Override
    public double getGyroRate() {
        return 0;
    }
}
