package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.PickerPosition;

public class NoopPicker extends AbstractPicker {

    @Override
    public void setPower(float power) {

    }

    @Override
    public void resetPower() {

    }

    @Override
    public double getPower() {
        return 0;
    }

    @Override
    public void setPosition(PickerPosition position) {

    }

    @Override
    public PickerPosition getPosition() {
        return PickerPosition.HOME;
    }
}
