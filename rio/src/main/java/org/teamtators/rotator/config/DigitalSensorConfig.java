package org.teamtators.rotator.config;

import org.teamtators.rotator.components.DigitalSensor;
import org.teamtators.rotator.components.SensorType;

public class DigitalSensorConfig {

    private int channel;
    private SensorType sensorType;

    public int getChannel() {
        return channel;
    }

    public SensorType getType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void create() {
        DigitalSensor digitalSensor = new DigitalSensor(channel, sensorType);
    }
}
