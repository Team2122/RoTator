package org.teamtators.rotator.config;

import org.teamtators.rotator.components.AnalogPotentiometer;

public class AnalogPotentiometerConfig {
    public int channel;
    public double scale;
    public double offset;

    public AnalogPotentiometer create() {
        return new AnalogPotentiometer(channel, scale, offset);
    }
}

