package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.ILogitechF310;
import org.teamtators.rotator.subsystems.IDrive;

public class DriveTank extends CommandBase {
    public DriveTank() {
        super("DriveTank");
    }

    private ILogitechF310 joystick;
    private IDrive drive;

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
        double leftPower = joystick.getAxisValue(ILogitechF310.AxisKind.LEFT_STICK_Y);
        double rightPower = joystick.getAxisValue(ILogitechF310.AxisKind.RIGHT_STICK_Y);

        drive.setPowers((float) leftPower, (float) rightPower);

        return false;
    }
}
