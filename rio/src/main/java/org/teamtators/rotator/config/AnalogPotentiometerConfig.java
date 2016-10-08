package org.teamtators.rotator.config;

import org.teamtators.rotator.components.AnalogPotentiometer;

public class AnalogPotentiometerConfig {
    public int channel;
    public double scale = 1.0;
    public double offset = 0.0;

    public AnalogPotentiometer create() {
        return new AnalogPotentiometer(channel, scale, offset);
    }
}

