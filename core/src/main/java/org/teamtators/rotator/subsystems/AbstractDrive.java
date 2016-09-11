package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.IGyro;
import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for the drive train which drives the robot
 */
public abstract class AbstractDrive extends Subsystem {
    public AbstractDrive() {
        super("Drive");
    }

    /**
     * It sets the power to the  drivetrain motors
     *
     * @param leftPower  power for the left motor, between 0 and 1
     * @param rightPower power for the right motor, btwn 0 and 1
     */
    public void setPowers(float leftPower, float rightPower) {
        setLeftPower(leftPower);
        setRightPower(rightPower);
    }

    /**
     * It sets the power to the left drivetrain motor
     *
     * @param leftPower  power for the left motor, between 0 and 1
     */
    public abstract void setLeftPower(float leftPower);

    /**
     * It sets the power to the right drivetrain motors
     *
     * @param rightPower power for the right drivetrain motor, between 0 and 1
     */
    public abstract void setRightPower(float rightPower);

    /**
     * resets the power for the motors to 0
     */
    public void resetPowers() {
        setPowers(0f, 0f);
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
     * @return distance the left side of the drivetrain traveled in inches
     */
    public abstract double getLeftDistance();

    /**
     * @return distance the right side of the drivetrain traveled in inches
     */
    public abstract double getRightDistance();

    /**
     * @return the average distance traveled on either side. Also the distance traveled by the center of the drive train
     */
    public double getAverageDistance() {
        return (getLeftDistance() + getRightDistance()) / 2;
    }

    /**
     * resets the distance and speed on the encoders
     */
    public abstract void resetEncoders();

    /**
     * Sets how the drive train motors are controlled
     *
     * @param driveMode the drive mode
     */
    public abstract void setDriveMode(DriveMode driveMode);

    public abstract IGyro getGyro();

    public abstract double getGyroAngle();

    public abstract void resetGyroAngle();

    public abstract double getGyroRate();
}
