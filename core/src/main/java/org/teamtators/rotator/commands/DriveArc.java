package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.AbstractSteppable;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.datalogging.LogDataProvider;
import org.teamtators.rotator.subsystems.AbstractDrive;

import java.util.Arrays;
import java.util.List;

/**
 * Drive in an arc
 * Ported from Kartoshka's DriveZach
 */
public class DriveArc extends CommandBase implements Configurable<DriveArc.Config> {
    private Config config;
    private AbstractDrive drive;

    private Stepper stepper;
    private AbstractSteppable angleController;
    private DataCollector dataCollector;

    public DriveArc(CoreRobot robot) {
        super("DriveArc");
        drive = robot.drive();
        stepper = robot.stepper();
        dataCollector = robot.dataCollector();
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
        public boolean dataLogging = false;
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
        private LogDataProvider logDataProvider;

        public DriveArcController() {
        }

        public synchronized boolean isOnTarget() {
            return this.angleError <= config.angleTolerance || currentDistance >= config.distance;
        }

        @Override
        public void onEnable() {
            lastGyroAngle = gyroAngle = drive.getGyroAngle();
            currentDistance = startDistance = drive.getAverageDistance();
            double angleDelta = config.endAngle - config.startAngle;
            desiredRate = angleDelta / config.distance;
            angleError = angleDelta;
            if (config.dataLogging)
                dataCollector.startProvider(getLogDataProvider());
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
            if (logDataProvider == null)
                logDataProvider = new LogDataProvider() {
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
                };
            return logDataProvider;
        }

        @Override
        public void onDisable() {
            drive.resetSpeeds();
            dataCollector.stopProvider(getLogDataProvider());
        }
    }
}
