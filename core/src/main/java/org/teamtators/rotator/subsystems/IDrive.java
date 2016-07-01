package org.teamtators.rotator.subsystems;

/**
 * Interface for the drive train which drives the robot
 */
public interface IDrive {
    /**
     * It sets the power to the  drivetrain motors
     *
     * @param leftPower  power for the left motor, between 0 and 1
     * @param rightPower power for the right motor, btwn 0 and 1
     */
    void setPowers(float leftPower, float rightPower);

    /**
     * resets the power for the motors to 0
     */
    default void resetPowers() {
        setPowers(0f, 0f);
    }

    /**
     * @return distance the left side of the drivetrain traveled in inches
     */
    double getLeftDistance();

    /**
     * @return distance the right side of the drivetrain traveled in inches
     */
    double getRightDistance();

    /**
     * @return the average distance traveled on either side. Also the distance traveled by the center of the drive train
     */
    default double getAverageDistance() {
        return (getLeftDistance() + getRightDistance()) / 2;
    }

    /**
     * resets the distance and speed on the encoders
     */
    void resetEncoders();

    /**
     * Sets how the drive train motors are controlled
     *
     * @param driveMode the drive mode
     */
    void setDriveMode(DriveMode driveMode);

}
