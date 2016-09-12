package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.subsystems.AbstractDrive;

import javax.inject.Inject;

public class DriveTank extends CommandBase {
    private AbstractDrive drive;
    private LogitechF310 driverJoystick;

    @Inject
    public DriveTank(AbstractDrive drive, AbstractOperatorInterface operatorInterface) {
        super("DriveTank");
        this.drive = drive;
        requires(drive);
        validIn(RobotState.TELEOP);
        driverJoystick = operatorInterface.driverJoystick();
    }

    @Override
    protected void finish(boolean interrupted) {
        drive.resetSpeeds();
        super.finish(interrupted);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected boolean step() {
        double leftPower = -driverJoystick.getAxisValue(LogitechF310.Axis.LEFT_STICK_Y);
        double rightPower = -driverJoystick.getAxisValue(LogitechF310.Axis.RIGHT_STICK_Y);

        drive.setSpeeds(leftPower, rightPower);

        return false;
    }
}
