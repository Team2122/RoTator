package org.teamtators.rotator;

/**
 * Interface for gyroscopes
 */
public interface IGyro {
    void setUpdatePeriod(double updatePeriod);

    double getUpdatePeriod();

    void setCalibrationPeriod(double calibrationPeriod);

    double getCalibrationPeriod();

    /**
     * Resets everything
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
     * Gets the current calibration rate
     *
     * @return The current calibration rate
     */
    float getCalibrationRate();

    /**
     * Checks if the gyro is currently calibrating
     *
     * @return
     */
    boolean isCalibrating();

    /**
     * Gets the rate from the gyro
     *
     * @return The rate in degrees per second, positive is clockwise
     */
    double getRate();

    /**
     * Gets the angle of the gyro
     *
     * @return The angle of the gyro in degrees
     */
    float getAngle();

    /**
     * Resets the angle of the gyro to zero
     */
    void reset();
}
