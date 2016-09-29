package org.teamtators.rotator.tester.components;

import org.teamtators.rotator.components.DigitalSensor;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class DigitalSensorTest extends ComponentTest {

    private DigitalSensor digitalSensor;

    public DigitalSensorTest(String name, DigitalSensor digitalSensor) {
        super(name);
        this.digitalSensor = digitalSensor;
    }

    @Override
    public void start() {
        logger.info(">>>>Press 'A' to get the value and type from the sensor");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        if (button == LogitechF310.Button.A) {
            logger.info(">>>>Digital sensor value {} (type {})", digitalSensor.get(), digitalSensor.getType());
        }
    }
}
