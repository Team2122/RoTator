package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.subsystems.AbstractPicker;

public class NoopPicker extends AbstractPicker {

    @Override
    public void setPower(double power) {

    }

    @Override
    public boolean isAtCheval() {
        return false;
    }
}
