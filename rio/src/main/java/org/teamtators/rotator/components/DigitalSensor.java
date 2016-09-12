package org.teamtators.rotator.components;

import edu.wpi.first.wpilibj.DigitalInput;

public class DigitalSensor {

    private DigitalInput digitalSensor;
    private SensorType type;

    public DigitalSensor(int channel, SensorType sensorType) {
        digitalSensor = new DigitalInput(channel);
        this.type = sensorType;
    }

    public boolean get() {
        boolean value = getRawValue();
        switch (type) {
            case NPN:
                return !value;
            default:
                return value;
        }
    }

    public boolean getRawValue() {
        return digitalSensor.get();
    }

    public SensorType getType() {
        return type;
    }

}
