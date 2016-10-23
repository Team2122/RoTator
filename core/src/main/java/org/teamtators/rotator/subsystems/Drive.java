package org.teamtators.rotator.subsystems;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.ControllerTest;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.scheduler.StateListener;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.subsystems.impl.AbstractDrive;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Interface for the drive train which drives the robot
 */
public final class Drive extends Subsystem implements Configurable<Drive.Config>, ITestable, StateListener {
    private final AbstractDrive driveImpl;

    private AbstractController leftController = null;
    private AbstractController rightController = null;
    private DriveMode driveMode = DriveMode.DIRECT;
    private double maxSpeed;
    private boolean hasCalibratedGyro;

    @Inject
    ControllerFactory controllerFactory;

    @Inject
    public Drive(AbstractDrive driveImpl) {
        super("Drive");
        this.driveImpl = driveImpl;
    }

    /**
     * Sets powers to the drive train directly. Power is from -1 to 1, positive is forward
     * @param leftPower The power for the left side
     * @param rightPower The power for the right side
     */
    public void setDirectPowers(double leftPower, double rightPower) {
        setDriveMode(DriveMode.DIRECT);
        driveImpl.setLeftPower(leftPower);
        driveImpl.setRightPower(rightPower);
    }

    /**
     * Resets the powers outputed to the drive train to 0.0. Ie. not moving
     */
    public void resetPowers() {
        setDirectPowers(0, 0);
    }

    /**
     * It sets the speed to the drivetrain motors
     *
     * @param leftSpeed  speed for the left motor in inches per second
     * @param rightSpeed speed for the right motor in inches per second
     */
    public void setSpeeds(double leftSpeed, double rightSpeed) {
        setDriveMode(DriveMode.VELOCITY);
        leftController.setSetpoint(leftSpeed);
        rightController.setSetpoint(rightSpeed);
    }

    public DriveMode getDriveMode() {
        return driveMode;
    }

    /**
     * Sets how the drive train motors are controlled
     *
     * @param driveMode the drive mode
     */
    public void setDriveMode(DriveMode driveMode) {
        checkNotNull(driveMode);
        if (getDriveMode() == driveMode) return;
        this.driveMode = driveMode;
        switch (driveMode) {
            default:
            case DISABLED:
            case DIRECT:
                leftController.disable();
                rightController.disable();
                break;
            case VELOCITY:
                leftController.enable();
                rightController.enable();
                break;
        }
    }

    /**
     * Gets the current angle of the gyro, relative to the orientation of the robot
     * when resetGyroAngle() was last called
     *
     * @return The angle, in degrees
     */
    public double getGyroAngle() {
        return driveImpl.getGyroAngle();
    }

    /**
     * Resets the current gyro angle to 0
     */
    public void resetGyroAngle() {
        driveImpl.resetGyroAngle();
    }

    /**
     * Gets the current rate of rotation of the gyro
     *
     * @return The rate, in degrees per second
     */
    public double getGyroRate() {
        return driveImpl.getGyroRate();
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public void onEnterState(RobotState newState) {
        switch (newState) {
            case TELEOP:
            case AUTONOMOUS:
                if (!hasCalibratedGyro) {
                    driveImpl.getGyro().finishCalibration();
                    hasCalibratedGyro = true;
                }
                resetGyroAngle();
                break;
            case DISABLED:
            case TEST:
                setDriveMode(DriveMode.DISABLED);
                driveImpl.getGyro().startCalibration();
                break;
        }
    }

    public double getAverageDistance() {
        return driveImpl.getAverageDistance();
    }

    public static class Config {
        public JsonNode controller;
        public double maxSpeed;
    }

    @Override
    public void configure(Config config) {
        leftController = controllerFactory.create(config.controller);
        leftController.setName("leftDrive");
        leftController.setInputProvider(driveImpl::getLeftRate);
        leftController.setOutputConsumer(driveImpl::setLeftPower);

        rightController = controllerFactory.create(config.controller);
        rightController.setName("rightDrive");
        rightController.setInputProvider(driveImpl::getRightRate);
        rightController.setOutputConsumer(driveImpl::setRightPower);

        setMaxSpeed(config.maxSpeed);
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        ComponentTestGroup testGroup = driveImpl.getTestGroup();
        testGroup.addTest(new ControllerTest(leftController, getMaxSpeed()));
        testGroup.addTest(new ControllerTest(rightController, getMaxSpeed()));
        return testGroup;
    }
}
