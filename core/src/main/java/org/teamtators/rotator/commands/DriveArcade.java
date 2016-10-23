package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.subsystems.Drive;

public class DriveArcade extends CommandBase implements Configurable<DriveArcade.Config> {
    private Config config;
    private Drive drive;
    private LogitechF310 driverJoystick;

    public DriveArcade(CoreRobot robot) {
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
        double speed = -driverJoystick.getAxisValue(LogitechF310.Axis.RIGHT_STICK_Y);
        double rotate = driverJoystick.getAxisValue(LogitechF310.Axis.RIGHT_STICK_X);
        speed = DriveUtils.applyDriveModifiers(speed,
                config.deadzone, config.exponent) * config.speedMultiplier;
        rotate = DriveUtils.applyDriveModifiers(rotate,
                config.deadzone, config.exponent) * config.rotateMultiplier;

        double leftSpeed = speed + rotate;
        double rightSpeed = speed - rotate;

        drive.setSpeeds(leftSpeed, rightSpeed);

        return false;
    }

    static class Config {
        public double deadzone;
        public double speedMultiplier;
        public double rotateMultiplier;
        public double exponent;
    }
}
