package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.ILogitechF310;
import org.teamtators.rotator.subsystems.AbstractDrive;

import javax.inject.Inject;

public class DriveTank extends CommandBase {
    @Inject
    AbstractDrive drive;
    @Inject
    ILogitechF310 driverJoystick;

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
    }

    @Override
    protected boolean step() {
        double leftPower = driverJoystick.getAxisValue(ILogitechF310.AxisKind.LEFT_STICK_Y);
        double rightPower = driverJoystick.getAxisValue(ILogitechF310.AxisKind.RIGHT_STICK_Y);

        drive.setPowers((float) leftPower, (float) rightPower);

        return false;
    }
}
