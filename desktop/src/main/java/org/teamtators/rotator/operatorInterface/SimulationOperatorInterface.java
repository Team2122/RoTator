package org.teamtators.rotator.operatorInterface;

public class SimulationOperatorInterface extends AbstractOperatorInterface {
    private LogitechF310 driverJoystick;

    public SimulationOperatorInterface(LogitechF310 joystick) {
        driverJoystick = joystick;
    }

    @Override
    public LogitechF310 driverJoystick() {
        return driverJoystick;
    }

    public SimulationOperatorInterface setDriverJoystick(LogitechF310 driverJoystick) {
        this.driverJoystick = driverJoystick;
        return this;
    }
}
