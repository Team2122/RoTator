package org.teamtators.rotator.operatorInterface;

import org.teamtators.rotator.scheduler.Subsystem;

public abstract class AbstractOperatorInterface extends Subsystem {
    public AbstractOperatorInterface() {
        super("OperatorInterface");
    }

    public abstract LogitechF310 driverJoystick();
}
