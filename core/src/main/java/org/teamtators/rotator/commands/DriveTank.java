package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.subsystems.AbstractDrive;

public class DriveTank extends CommandBase {
    private AbstractDrive drive;
    private LogitechF310 driverJoystick;

    public DriveTank(CoreRobot robot) {
        super("DriveTank");
        this.drive = robot.drive();
        driverJoystick = robot.operatorInterface().driverJoystick();
        requires(drive);
        validIn(RobotState.TELEOP);
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
