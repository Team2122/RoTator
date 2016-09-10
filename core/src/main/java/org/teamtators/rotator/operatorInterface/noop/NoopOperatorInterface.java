package org.teamtators.rotator.operatorInterface.noop;

import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;

public class NoopOperatorInterface extends AbstractOperatorInterface {
    private NoopLogitechF310 driverJoystick = new NoopLogitechF310();
    private NoopLogitechF310 gunnerJoystick = new NoopLogitechF310();

    @Override
    public LogitechF310 driverJoystick() {
        return driverJoystick;
    }

    @Override
    public LogitechF310 gunnerJoystick() {
        return gunnerJoystick;
    }
}
