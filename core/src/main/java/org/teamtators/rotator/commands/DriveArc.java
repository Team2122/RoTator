package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.AbstractSteppable;
import org.teamtators.rotator.control.PIDController;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.datalogging.LogDataProvider;
import org.teamtators.rotator.subsystems.AbstractDrive;

import java.util.Arrays;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Drive in an arc
 * Ported from Kartoshka's DriveZach
 */
public class DriveArc extends CommandBase implements Configurable<DriveArc.Config> {
    private Config config;
    private AbstractDrive drive;

    private Stepper stepper;
    private AbstractSteppable angleController;

    public DriveArc(CoreRobot robot) {
        super("DriveArc");
        drive = robot.drive();
        stepper = robot.stepper();
    }

    @Override
    protected boolean step() {
        return false;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        this.angleController = new DriveArcController();
        angleController.setStepper(stepper);
    }

    @Override
    protected void initialize() {
        super.initialize();
        angleController.enable();
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        angleController.disable();
    }

    static class Config {
        public double speed;
        public double distance;
        public double startAngle;
        public double endAngle;
        public double kAngle = .02;
        public double kRate = .02;
        public double angleTolerance = .5;
    }

    private class DriveArcController extends AbstractSteppable {
        private double startDistance;
        private double desiredRate;
        private double currentDistance;
        private double lastGyroAngle;
        private double gyroAngle;
        private double angleError;
        private double rampedSpeed;
        private double gyroRate;
        private double desiredAngle;

        public DriveArcController() {
        }

        public synchronized boolean isOnTarget() {
            return this.angleError <= config.angleTolerance || currentDistance >= config.distance;
        }

        @Override
        public void onEnable() {
            super.onEnable();
            lastGyroAngle = gyroAngle = drive.getGyroAngle();
            currentDistance = startDistance = drive.getAverageDistance();
            double angleDelta = config.endAngle - config.startAngle;
            desiredRate = angleDelta / config.distance;
            angleError = angleDelta;
        }

        @Override
        public void step(double delta) {
            currentDistance = Math.abs(startDistance - drive.getAverageDistance());

            rampedSpeed = config.speed; // TODO: should use a trapezoidal controller

            desiredAngle = desiredRate * currentDistance + config.startAngle;
            gyroAngle = drive.getGyroAngle();
            double angleDelta = desiredAngle - gyroAngle;

            gyroRate = (gyroAngle - lastGyroAngle) / delta;
            lastGyroAngle = gyroAngle;

            double offset = angleDelta * config.kAngle + desiredRate * config.kRate * rampedSpeed;
            drive.setSpeeds(rampedSpeed + offset, rampedSpeed - offset);
        }

        protected LogDataProvider getLogDataProvider() {
            return new LogDataProvider() {
                @Override
                public String getName() {
                    return DriveArc.this.getName();
                }

                @Override
                public List<Object> getKeys() {
                    return Arrays.asList("gyroAngle", "desiredAngle", "gyroRate", "desiredRate", "currentDistance");
                }

                @Override
                public List<Object> getValues() {
                    return Arrays.asList(gyroAngle, desiredAngle, gyroRate, desiredRate, currentDistance);
                }
            }
        }

        @Override
        public void onDisable() {
            super.onDisable();
            drive.resetSpeeds();
        }
    }
}
