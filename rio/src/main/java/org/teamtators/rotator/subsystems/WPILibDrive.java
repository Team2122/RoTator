package org.teamtators.rotator.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.EncoderConfig;
import org.teamtators.rotator.config.VictorSPConfig;

public class WPILibDrive extends AbstractDrive implements Configurable<WPILibDrive.Config> {
    public static class Config {
        public VictorSPConfig leftMotor;
        public VictorSPConfig rightMotor;
        public EncoderConfig leftEncoder;
        public EncoderConfig rightEncoder;
    }

    private VictorSP leftMotor;
    private VictorSP rightMotor;
    private Encoder leftEncoder;
    private Encoder rightEncoder;

    public WPILibDrive() {
    }

    @Override
    public void configure(Config config) {
        this.leftMotor = config.leftMotor.create();
        this.rightMotor = config.rightMotor.create();
        this.leftEncoder = config.leftEncoder.create();
        this.rightEncoder = config.rightEncoder.create();
    }

    @Override
    public void setLeftPower(float leftPower) {
        leftMotor.set(leftPower);
    }

    @Override
    public void setRightPower(float rightPower) {
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
