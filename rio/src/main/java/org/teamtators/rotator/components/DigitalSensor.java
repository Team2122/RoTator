package org.teamtators.rotator.components;

import edu.wpi.first.wpilibj.DigitalInput;
import org.teamtators.rotator.components.SensorType;

public class DigitalSensor {

    private DigitalInput digitalSensor;
    private SensorType sensorType;

    public DigitalSensor(int channel, SensorType sensorType) {
        digitalSensor = new DigitalInput(channel);
        this.sensorType = sensorType;
    }

    public boolean get() {
        boolean value = getRawValue()  ;
        if (sensorType == SensorType.NPN) {
            return !value;
        } else {
            return value;
        }
    }

    public boolean getRawValue() {
        return digitalSensor.get();
    }

    public SensorType getSensorType() {
        return sensorType;
    }

}
