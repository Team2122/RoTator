package org.teamtators.rotator.tester.components;

import org.teamtators.rotator.ADXRS453;
import org.teamtators.rotator.IGyro;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class GyroTest extends ComponentTest {
    private ADXRS453 gyro;

    public GyroTest(String name, ADXRS453 gyro) {
        super(name);
        this.gyro = gyro;
    }

    @Override
    public void start() {
        logger.info("Press A to get the rate, the angle, and the calibration offset of the gyro");
        logger.info("B to start calibration, and Y to end calibration");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        switch (button) {
            case A:
                logger.info("Gyro Angle: {}; Rate: {}; Calibration Offset: {}", gyro.getAngle(), gyro.getRate(),
                        gyro.getCalibrationOffset());
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

        }
    }
}
