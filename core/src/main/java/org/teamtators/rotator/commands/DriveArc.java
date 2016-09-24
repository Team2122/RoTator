package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractDrive;

/**
 * Drive in an arc
 * Ported from Kartoshka's DriveZach
 */
public class DriveArc extends CommandBase implements Configurable<DriveArc.Config> {

    private Config config;
    private AbstractDrive drive;

    private double desiredRate = 0;
    private double startDistance = 0;
    private double currentDistance = 0;
    private double gyroAngle = 0;

    public DriveArc(CoreRobot robot) {
        super("DriveArc");
        drive = robot.drive();
    }

    @Override
    protected boolean step() {
        currentDistance = Math.abs(startDistance - drive.getAverageDistance());
        double distanceLeft = config.distance - currentDistance;
        double rampedSpeed = config.speed;
        if (config.rampDistance != 0 && distanceLeft <= config.rampDistance) {
            double percentage = distanceLeft / config.rampDistance;
            rampedSpeed *= Math.pow(percentage, config.rampPower);
        }

        double desiredAngle = desiredRate * currentDistance + config.startAngle;
        gyroAngle = drive.getGyroAngle();
        double angleDelta = desiredAngle - gyroAngle;

        double gyroRate = drive.getGyroRate();
        double offset = angleDelta * config.angle + desiredRate * config.rate * rampedSpeed;
        drive.setSpeeds(rampedSpeed + offset, rampedSpeed - offset);

        double angleError = Math.abs(config.endAngle - gyroAngle);
        if (angleError <= config.angleTolerance) {
            return true;
        }
        if (currentDistance >= config.distance) {
            return true;
        }
        return false;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    static class Config {
        public double speed;
        public double distance;
        public double startAngle;
        public double endAngle;
        public double rampDistance = 0;
        public double rampPower = 0;
        public double angle = .02;
        public double rate = .02;
        public double angleTolerance = .5;
    }

    @Override
    protected void initialize() {
        super.initialize();
        startDistance = drive.getAverageDistance();
        desiredRate = (config.endAngle - config.startAngle) / config.distance;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        drive.resetSpeeds();
    }
}
