package org.teamtators.rotator.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;

public class WPILibDrive implements IDrive {
    private VictorSP leftMotor;
    private VictorSP rightMotor;
    private Encoder leftEncoder;
    private Encoder rightEncoder;

    public WPILibDrive() {
        leftMotor = new VictorSP(0);
        rightMotor = new VictorSP(1);
        leftEncoder = new Encoder(0, 1);
        rightEncoder = new Encoder(2, 3);
    }

    @Override
    public void setPowers(float leftPower, float rightPower) {
        leftMotor.set(leftPower);
        rightMotor.set(rightPower);
    }

    @Override
    public double getLeftDistance() {
        return leftEncoder.getDistance();
    }

    @Override
    public double getRightDistance() {
        return rightEncoder.getDistance();
    }

    @Override
    public void resetEncoders() {
        leftEncoder.reset();
        rightEncoder.reset();
    }

    @Override
    public void setDriveMode(DriveMode driveMode) {
        switch (driveMode) {
            case DIRECT:
                break;
            case PID:
                throw new IllegalStateException("PID mode is not implemented yet");
        }
    }
}
