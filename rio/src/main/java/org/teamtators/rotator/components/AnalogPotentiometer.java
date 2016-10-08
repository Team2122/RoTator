package org.teamtators.rotator.components;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.ControllerPower;

import static com.google.common.base.Preconditions.checkNotNull;

public class AnalogPotentiometer {
    private AnalogInput analogInput;
    private double scale = 1.0;
    private double offset = 0.0;

    public AnalogPotentiometer(AnalogInput analogInput) {
        checkNotNull(analogInput);
        this.analogInput = analogInput;
    }

    public AnalogPotentiometer(AnalogInput analogInput, double scale, double offset) {
        this(analogInput);
        this.scale = scale;
        this.offset = offset;
    }

    public AnalogPotentiometer(int channel) {
        this(new AnalogInput(channel));
    }

    public AnalogPotentiometer(int channel, double scale, double offset) {
        this(new AnalogInput(channel), scale, offset);
    }

    public AnalogInput getAnalogInput() {
        return analogInput;
    }

    public double getScale() {
        return scale;
    }

    public AnalogPotentiometer setScale(double scale) {
        this.scale = scale;
        return this;
    }

    public double getOffset() {
        return offset;
    }

    public AnalogPotentiometer setOffset(double offset) {
        this.offset = offset;
        return this;
    }

    public double getVoltage() {
        return analogInput.getVoltage();
    }

    public double getValue() {
        double x = getVoltage() / ControllerPower.getVoltage5V();
        return (x * scale) + offset;
    }
}
