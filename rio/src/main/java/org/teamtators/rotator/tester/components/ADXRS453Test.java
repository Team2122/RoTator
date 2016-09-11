package org.teamtators.rotator.tester.components;

import org.teamtators.rotator.ADXRS453;
import org.teamtators.rotator.IGyro;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class ADXRS453Test extends ComponentTest {
    private ADXRS453 gyro;

    public ADXRS453Test(String name, ADXRS453 gyro) {
        super(name);
        this.gyro = gyro;
    }

    @Override
    public void start() {
        logger.info("Press A to get the rate, the angle, and the calibration offset of the gyro");
        logger.info("B to start calibration, and Y to end calibration, X to get detailed information");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        switch (button) {
            case A:
                double angle = gyro.getAngle();
                double rate = gyro.getRate();
                double calibrationOffset = gyro.getCalibrationOffset();
                logger.info("Gyro Angle: {}, Rate: {} (Offset: {})", angle, rate, calibrationOffset);
                break;
            case B:
                logger.info("Starting gyro calibration");
                gyro.startCalibration();
                break;
            case Y:
                logger.info("Finishing gyro calibration");
                gyro.finishCalibration();
                break;
            case X:
                int serial = gyro.getSerialNumber();
                double temperature = gyro.getTemperature();
                logger.info("Serial: 0x{}, temperature: {} C", Integer.toHexString(serial), temperature);
                break;
        }
    }
}
