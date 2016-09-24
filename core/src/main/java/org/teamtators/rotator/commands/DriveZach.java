package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractDrive;

public class DriveZach extends CommandBase implements Configurable<DriveZach.Config> {

    private double angle = .02;
    private double rate = .02;
    private double angleTolerance = .5;

    private Config config;
    private AbstractDrive drive;

    private double rampDistance;
    private double rampPower;

    private double desiredRate = 0;
    private double startDistance = 0;
    private double currentDistance = 0;
    private double gyroAngle = 0;

    public DriveZach(CoreRobot robot) {
        super("DriveZach");
        drive = robot.drive();
    }

    @Override
    protected boolean step() {
        currentDistance = Math.abs(startDistance - drive.getAverageDistance());
        double distanceLeft = config.distance - currentDistance;
        double rampedSpeed = config.speed;
        if (rampDistance != 0 && distanceLeft <= rampDistance) {
            double percentage = distanceLeft / rampDistance;
            rampedSpeed *= Math.pow(percentage, rampPower);
        }

        double desiredAngle = desiredRate * currentDistance + config.startAngle;
        gyroAngle = drive.getGyroAngle();
        double angleDelta = desiredAngle - gyroAngle;

        double gyroRate = drive.getGyroRate();
        double offset = angleDelta * angle + desiredRate * rate * rampedSpeed;
        drive.setSpeeds(rampedSpeed + offset, rampedSpeed - offset);

        double angleError = Math.abs(config.endAngle - gyroAngle);
        if(angleError <= angleTolerance) {
            return true;
        }
        if(currentDistance >= config.distance) {
            return true;
        }
        return false;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        if (config.ramp != null) {
            rampDistance = config.ramp.get("rampDistance").asDouble();
            rampPower = config.ramp.get("rampPower").asDouble();
        } else {
            rampDistance = 0;
            rampPower = 0;
        }
    }

    static class Config {
        public double speed;
        public double distance;
        public double startAngle;
        public double endAngle;
        public JsonNode ramp;
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
