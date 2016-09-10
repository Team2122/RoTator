package org.teamtators.rotator.operatorInterface;

public class SimulationOperatorInterface extends AbstractOperatorInterface {
    private LogitechF310 driverJoystick;
    private LogitechF310 gunnerJoystick;

    public SimulationOperatorInterface(LogitechF310 driverJoystick, LogitechF310 gunnerJoystick) {
        this.driverJoystick = driverJoystick;
        this.gunnerJoystick = gunnerJoystick;
    }

    @Override
    public LogitechF310 driverJoystick() {
        return driverJoystick;
    }

    @Override
    public LogitechF310 gunnerJoystick() {
        return gunnerJoystick;
    }

    public SimulationOperatorInterface setDriverJoystick(LogitechF310 driverJoystick) {
        this.driverJoystick = driverJoystick;
        return this;
    }

    public SimulationOperatorInterface setGunnerJoystick(LogitechF310 gunnerJoystick) {
        this.gunnerJoystick = gunnerJoystick;
        return this;
    }

}
