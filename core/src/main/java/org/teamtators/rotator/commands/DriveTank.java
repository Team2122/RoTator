package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.subsystems.AbstractDrive;

import javax.inject.Inject;

public class DriveTank extends CommandBase {
    @Inject
    private AbstractDrive drive;
    @Inject
    private AbstractOperatorInterface operatorInterface;

    private LogitechF310 driverJoystick;

    public DriveTank() {
        super("DriveTank");
    }

    @Override
    protected void finish(boolean interrupted) {
        drive.resetPowers();
        super.finish(interrupted);
    }

    @Override
    protected void initialize() {
        super.initialize();
        driverJoystick = operatorInterface.driverJoystick();
    }

    @Override
    protected boolean step() {
        double leftPower = driverJoystick.getAxisValue(LogitechF310.Axis.LEFT_STICK_Y);
        double rightPower = driverJoystick.getAxisValue(LogitechF310.Axis.RIGHT_STICK_Y);

        drive.setPowers((float) leftPower, (float) rightPower);

        return false;
    }
}
