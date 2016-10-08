package org.teamtators.rotator.tester.components;

import org.teamtators.rotator.components.AnalogPotentiometer;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class AnalogPotentiometerTest extends ComponentTest {
    private AnalogPotentiometer analogPotentiometer;
    private double originalOffset;

    public AnalogPotentiometerTest(String name, AnalogPotentiometer analogPotentiometer) {
        super(name);
        this.analogPotentiometer = analogPotentiometer;
        this.originalOffset = analogPotentiometer.getOffset();
    }

    @Override
    public void start() {
        logger.info(">>>> Press A to get the potentiometer value. B to get scale and offset");
        logger.info(">>>> X to reset offset and Y to apply original offset.");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        switch (button) {
            case A:
                double value = analogPotentiometer.getValue();
                double voltage = analogPotentiometer.getVoltage();
                logger.info("Value: {} (voltage: {}V)", value, voltage);
                break;
            case X:
                analogPotentiometer.setOffset(0);
                logger.info("Reset offset to 0");
                break;
            case Y:
                analogPotentiometer.setOffset(originalOffset);
                logger.info("Set offset to original value of {}", originalOffset);
                break;
            case B:
                double offset = analogPotentiometer.getOffset();
                double scale = analogPotentiometer.getScale();
                logger.info("Offset: {}, Scale: {}", offset, scale);
                break;
        }
    }
}
