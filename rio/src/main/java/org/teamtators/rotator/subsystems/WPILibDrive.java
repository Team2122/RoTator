package org.teamtators.rotator.subsystems;

import com.fasterxml.jackson.databind.JsonNode;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.components.ADXRS453;
import org.teamtators.rotator.components.Gyro;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.config.EncoderConfig;
import org.teamtators.rotator.config.VictorSPConfig;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.scheduler.StateListener;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.components.ADXRS453Test;
import org.teamtators.rotator.tester.components.EncoderTest;
import org.teamtators.rotator.tester.components.VictorSPTest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WPILibDrive extends AbstractDrive implements Configurable<WPILibDrive.Config>, ITestable, StateListener {
    private VictorSP leftMotor;
    private VictorSP rightMotor;
    private Encoder leftEncoder;
    private Encoder rightEncoder;
    private ADXRS453 gyro;
    private DriveMode defaultDriveMode;

    @Inject
    private ControllerFactory controllerFactory;

    public WPILibDrive() {
    }

    @Override
    public void configure(Config config) {
        this.leftMotor = config.leftMotor.create();
        this.rightMotor = config.rightMotor.create();
        this.leftEncoder = config.leftEncoder.create();
        this.rightEncoder = config.rightEncoder.create();
        this.gyro = new ADXRS453(SPI.Port.kOnboardCS0);

        gyro.start();
        gyro.startCalibration();

        setLeftController(controllerFactory.create(config.controller));
        setRightController(controllerFactory.create(config.controller));
        setMaxSpeed(config.maxSpeed);
        this.defaultDriveMode = config.driveMode;
    }

    @Override
    public void setLeftPower(double leftPower) {
        leftMotor.set(leftPower);
    }

    @Override
    public void setRightPower(double rightPower) {
        rightMotor.set(rightPower);
    }

    @Override
    public double getLeftRate() {
        return leftEncoder.getRate();
    }

    @Override
    public double getRightRate() {
        return rightEncoder.getRate();
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
    public Gyro getGyro() {
        return gyro;
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        return new ComponentTestGroup("Drive",
                new VictorSPTest("leftMotor", leftMotor),
                new VictorSPTest("rightMotor", rightMotor),
                new EncoderTest("leftEncoder", leftEncoder),
                new EncoderTest("rightEncoder", rightEncoder),
                new ADXRS453Test("gyro", gyro));
    }

    @Override
    public void onEnterState(RobotState newState) {
        switch (newState) {
            case TELEOP:
            case AUTONOMOUS:
                setDriveMode(defaultDriveMode);
                break;
            case DISABLED:
            case TEST:
                setDriveMode(DriveMode.DISABLED);
                break;
        }
    }

    public static class Config {
        public VictorSPConfig leftMotor;
        public VictorSPConfig rightMotor;
        public EncoderConfig leftEncoder;
        public EncoderConfig rightEncoder;
        public DriveMode driveMode;
        public double maxSpeed;
        public JsonNode controller;
    }
}
