package org.teamtators.rotator.operatorInterface;

import org.teamtators.rotator.scheduler.Subsystem;

public abstract class AbstractOperatorInterface extends Subsystem {
    public AbstractOperatorInterface() {
        super("OperatorInterface");
    }

    public abstract LogitechF310 driverJoystick();

    public abstract LogitechF310 gunnerJoystick();

    public LogitechF310 getJoystick(String joystickName) {
        switch (joystickName) {
            case "driver":
                return driverJoystick();
            case "gunner":
                return gunnerJoystick();
            default:
                throw new IllegalArgumentException("Invalid joystick '" + joystickName + "' requested");
        }
    }
}
