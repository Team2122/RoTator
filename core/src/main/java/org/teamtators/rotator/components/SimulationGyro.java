package org.teamtators.rotator.components;

public class SimulationGyro implements Gyro {
    private double calibrationPeriod = 5.0;
    private double rate;
    private double angle;
    private boolean calibrating;

    @Override
    public void setCalibrationPeriod(double calibrationPeriod) {
        this.calibrationPeriod = calibrationPeriod;
    }

    @Override
    public double getCalibrationPeriod() {
        return calibrationPeriod;
    }

    @Override
    public void fullReset() {

    }

    @Override
    public void startCalibration() {
        calibrating = true;
    }

    @Override
    public void finishCalibration() {
        calibrating = false;
    }

    @Override
    public double getCalibrationOffset() {
        return 0;
    }

    @Override
    public boolean isCalibrating() {
        return calibrating;
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    @Override
    public void resetAngle() {
        setAngle(0);
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
