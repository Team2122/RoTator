package org.teamtators.rotator.tester.components;

import org.teamtators.rotator.IGyro;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class GyroTest extends ComponentTest {

    private IGyro gyro;

    public GyroTest(String name, IGyro gyro) {
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
        if (button == LogitechF310.Button.A) {
            logger.info("Gyro Angle: {}; Rate: {}; Calibration Offset: {}", gyro.getAngle(), gyro.getRate(), gyro.getCalibrationOffset());
        } else if (button == LogitechF310.Button.B) {
            logger.info("Starting gyro calibration");
            gyro.startCalibration();
        } else if (button == LogitechF310.Button.Y) {
            logger.info("Finishing gyro calibration");
            gyro.calibrate();
        }
    }
}
