package org.teamtators.rotator;

/**
 * Sensor interface for gyroscopes. Contains methods for calibration and getting angle (yaw) information.
 */
public interface IGyro {
    /**
     * Set how often to get updates from the gyro
     *
     * @param updatePeriod Desired time between updates from gyro
     */
    void setUpdatePeriod(double updatePeriod);

    /**
     * Get how often updates are gotten from the gyro
     *
     * @return Time between updates from gyro
     */
    double getUpdatePeriod();

    /**
     * Set the amount of time the gyro will spend calibrating
     *
     * @param calibrationPeriod Desired length of gyro calibration period
     */
    void setCalibrationPeriod(double calibrationPeriod);

    /**
     * Get the amount of time the gyro spends calibrating
     *
     * @return Length of gyro calibration period
     */
    double getCalibrationPeriod();

    /**
     * Reset calibration and angle monitoring
     */
    void fullReset();

    /**
     * Starts calibrating the gyro. Resets the calibration value and begins
     * sampling gyro values to get the average 0 value. Sample time determined
     * by calibrationTicks
     */
    void startCalibration();

    /**
     * Finishes calibration. Stops calibrating and sets the calibration value.
     */
    void calibrate();

    /**
     * Gets the zero point for rate measurement
     *
     * @return The offset found by calibration
     */
    float getCalibrationOffset();

    /**
     * Checks if the gyro is currently calibrating.
     * If it is, measured rate and angle values are not guaranteed to be accurate.
     *
     * @return Whether the gyro is currently calibrating
     */
    boolean isCalibrating();

    /**
     * Gets the rate of yaw change from the gyro
     *
     * @return The rate of yaw change in degrees per second, positive is clockwise
     */
    double getRate();

    /**
     * Gets the yaw of the gyro
     *
     * @return The yaw of the gyro in degrees
     */
    float getAngle();

    /**
     * Resets the current angle of the gyro to zero
     */
    void reset();
}
