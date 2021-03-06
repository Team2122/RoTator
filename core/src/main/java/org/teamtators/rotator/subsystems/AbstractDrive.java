package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.components.Gyro;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.scheduler.Subsystem;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Interface for the drive train which drives the robot
 */
public abstract class AbstractDrive extends Subsystem {
    private AbstractController leftController = null;
    private AbstractController rightController = null;
    private DriveMode driveMode = DriveMode.DIRECT;
    private double maxSpeed;

    public AbstractDrive() {
        super("Drive");
    }

    /**
     * It sets the speed to the  drivetrain motors
     *
     * @param leftSpeed  speed for the left motor in inches per second
     * @param rightSpeed speed for the right motor in inches per second
     */
    public void setSpeeds(double leftSpeed, double rightSpeed) {
        setLeftSpeed(leftSpeed);
        setRightSpeed(rightSpeed);
    }

    /**
     * Set the speed of the left half of the drivetrain
     *
     * @param leftSpeed Left motor speed in inches per second
     */
    public void setLeftSpeed(double leftSpeed) {
        switch (driveMode) {
            case DIRECT:
                setLeftPower(leftSpeed / maxSpeed);
                break;
            case CONTROLLER:
                leftController.setSetpoint(leftSpeed);
                break;
        }
    }

    /**
     * Set the speed of the right half of the drivetrain
     *
     * @param rightSpeed Right motor speed in inches per second
     */
    public void setRightSpeed(double rightSpeed) {
        switch (driveMode) {
            case DIRECT:
                setRightPower(rightSpeed / maxSpeed);
                break;
            case CONTROLLER:
                rightController.setSetpoint(rightSpeed);
                break;
        }
    }

    /**
     * It sets the speed to the left drivetrain motor
     *
     * @param leftPower speed for the left motor, between 0 and 1
     */
    protected abstract void setLeftPower(double leftPower);

    /**
     * It sets the speed to the right drivetrain motors
     *
     * @param rightPower speed for the right drivetrain motor, between 0 and 1
     */
    protected abstract void setRightPower(double rightPower);

    /**
     * resets the speed for the motors to 0
     */
    public void resetSpeeds() {
        setSpeeds(0f, 0f);
    }

    /**
     * @return Rate of rotation of the left side of the drivetrain in inches per second
     */
    public abstract double getLeftRate();

    /**
     * @return Rate of rotation of the right side of the drivetrain in inches per second
     */
    public abstract double getRightRate();

    /**
     * @return The average rate of rotation on either side in inches per second
     */
    public double getAverageRate() {
        return (getLeftRate() + getRightRate()) / 2;
    }

    /**
     * @return ballDistance the left side of the drivetrain traveled in inches
     */
    public abstract double getLeftDistance();

    /**
     * @return ballDistance the right side of the drivetrain traveled in inches
     */
    public abstract double getRightDistance();

    /**
     * @return the average ballDistance traveled on either side. Also the ballDistance traveled by the center of the drive train
     */
    public double getAverageDistance() {
        return (getLeftDistance() + getRightDistance()) / 2;
    }

    /**
     * resets the ballDistance and speed on the encoders
     */
    public abstract void resetEncoders();

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
        this.driveMode = driveMode;
        switch (driveMode) {
            default:
            case DISABLED:
            case DIRECT:
                leftController.disable();
                rightController.disable();
                break;
            case CONTROLLER:
                leftController.enable();
                rightController.enable();
                break;
        }
    }

    /**
     * Gets the gyroscope sensor object to measure angle and angle change of the yaw axis
     * of the robot
     *
     * @return The gyro object
     */
    public abstract Gyro getGyro();

    /**
     * Gets the current angle of the gyro, relative to the orientation of the robot
     * when resetGyroAngle() was last called
     *
     * @return The angle, in degrees
     */
    public double getGyroAngle() {
        return getGyro().getAngle();
    }

    /**
     * Resets the current gyro angle to 0
     */
    public void resetGyroAngle() {
        getGyro().resetAngle();
    }

    /**
     * Gets the current rate of rotation of the gyro
     *
     * @return The rate, in degrees per second
     */
    public double getGyroRate() {
        return getGyro().getRate();
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    protected AbstractController getLeftController() {
        return leftController;
    }

    protected void setLeftController(AbstractController leftController) {
        leftController.setName("leftDrive");
        leftController.setInputProvider(this::getLeftRate);
        leftController.setOutputConsumer(this::setLeftPower);
        this.leftController = leftController;
    }

    protected AbstractController getRightController() {
        return rightController;
    }

    protected void setRightController(AbstractController rightController) {
        rightController.setName("rightDrive");
        rightController.setInputProvider(this::getRightRate);
        rightController.setOutputConsumer(this::setRightPower);
        this.rightController = rightController;
    }
}
