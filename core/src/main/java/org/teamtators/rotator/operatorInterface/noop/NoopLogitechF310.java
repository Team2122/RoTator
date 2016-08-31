package org.teamtators.rotator.operatorInterface.noop;

import org.teamtators.rotator.operatorInterface.LogitechF310;

public class NoopLogitechF310 implements LogitechF310 {
    @Override
    public double getAxisValue(Axis axisKind) {
        return 0;
    }

    @Override
    public boolean getButtonValue(Button button) {
        return false;
    }
}
