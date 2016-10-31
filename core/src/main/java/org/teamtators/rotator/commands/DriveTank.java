package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.operatorInterface.DriveOutput;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.subsystems.Drive;

public class DriveTank extends CommandBase implements Configurable<DriveTank.Config> {
    private Config config;
    private Drive drive;
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
        drive.resetPowers();
        super.finish(interrupted);
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected boolean step() {
        DriveOutput driveOutput = new DriveOutput(-driverJoystick.getAxisValue(LogitechF310.Axis.LEFT_STICK_Y),
                -driverJoystick.getAxisValue(LogitechF310.Axis.RIGHT_STICK_Y))
                .deadzone(config.deadzone)
                .exponent(config.exponent)
                .mul(config.multiplier);

        drive.setSpeeds(driveOutput);

        return false;
    }

    static class Config {
        public double deadzone;
        public double exponent;
        public DriveOutput multiplier;
    }
}
