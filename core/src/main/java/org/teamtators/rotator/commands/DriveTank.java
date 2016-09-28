package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.subsystems.AbstractDrive;

public class DriveTank extends CommandBase implements Configurable<DriveTank.Config> {
    private Config config;
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
    public void configure(Config config) {
        this.config = config;
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
        leftPower = DriveUtils.applyDriveModifiers(leftPower,
                config.deadzone, config.leftMultiplier, config.exponent);
        rightPower = DriveUtils.applyDriveModifiers(rightPower,
                config.deadzone, config.rightMultiplier, config.exponent);

        drive.setSpeeds(leftPower, rightPower);

        return false;
    }

    static class Config {
        public double deadzone;
        public double leftMultiplier;
        public double rightMultiplier;
        public double exponent;
    }
}
