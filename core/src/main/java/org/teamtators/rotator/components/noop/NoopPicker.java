package org.teamtators.rotator.components.noop;

import org.teamtators.rotator.components.AbstractPicker;

public class NoopPicker extends AbstractPicker {

    @Override
    public void setPickPower(double power) {

    }

    @Override
    public void setPinchPower(double power) {

    }

    @Override
    public boolean isAtCheval() {
        return false;
    }
}
