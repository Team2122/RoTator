package org.teamtators.rotator.subsystems.impl;

import org.teamtators.rotator.components.Gyro;
import org.teamtators.rotator.tester.ITestable;

public abstract class AbstractDrive implements ITestable {
    /**
     * It sets the speed to the left drivetrain motor
     *
     * @param leftPower speed for the left motor, between 0 and 1
     */
    public abstract void setLeftPower(double leftPower);

    /**
     * It sets the speed to the right drivetrain motors
     *
     * @param rightPower speed for the right drivetrain motor, between 0 and 1
     */
    public abstract void setRightPower(double rightPower);


    /**
     * @return Rate of rotation of the left side of the drivetrain in inches per second
     */
    public abstract double getLeftRate();

    /**
     * @return Rate of rotation of the right side of the drivetrain in inches per second
     */
    public abstract double getRightRate();

    /**
     * @return ballDistance the left side of the drivetrain traveled in inches
     */
    public abstract double getLeftDistance();

    /**
     * @return ballDistance the right side of the drivetrain traveled in inches
     */
    public abstract double getRightDistance();

    /**
     * resets the ballDistance and speed on the encoders
     */
    public abstract void resetEncoders();

    /**
     * @return The average rate of rotation on either side in inches per second
     */
    public double getAverageRate() {
        return (getLeftRate() + getRightRate()) / 2;
    }

    /**
     * @return the average ballDistance traveled on either side. Also the ballDistance traveled by the center of the drive train
     */
    public double getAverageDistance() {
        return (getLeftDistance() + getRightDistance()) / 2;
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
}