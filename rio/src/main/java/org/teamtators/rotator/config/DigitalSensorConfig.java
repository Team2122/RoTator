package org.teamtators.rotator.config;

import org.teamtators.rotator.components.DigitalSensor;
import org.teamtators.rotator.components.SensorType;

public class DigitalSensorConfig {

    private int channel;
    private SensorType type;

    public int getChannel() {
        return channel;
    }

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void create() {
        DigitalSensor digitalSensor = new DigitalSensor(channel, type);
    }
}
