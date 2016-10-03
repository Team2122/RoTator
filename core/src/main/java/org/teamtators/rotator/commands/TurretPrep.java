package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.datalogging.LogDataProvider;
import org.teamtators.rotator.subsystems.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TurretPrep extends CommandBase implements Configurable<TurretPrep.Config> {
    private Config config;
    private AbstractTurret turret;
    private AbstractVision vision;
    private AbstractPicker picker;
    private ITimeProvider timer;
    private int lastFrameNumber;
    private double deltaAngle;
    private double newAngle;
    private double currentAngle;

    private DataCollector dataCollector;
    private LogDataProvider logDataProvider = null;

    public TurretPrep(CoreRobot robot) {
        super("TurretPrep");
        this.turret = robot.turret();
        this.vision = robot.vision();
        this.picker = robot.picker();
        this.timer = robot.timeProvider();
        this.dataCollector = robot.dataCollector();
        requires(turret);
        requires(vision);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (picker.getPosition() == PickerPosition.HOME) {
            logger.warn("Picker is not out, not targeting");
            cancel();
            return;
        }
        // get correct wheel speed and hood position from distance to goal
        double goalDistance = vision.getVisionData().getDistance();
        TreeMap<Double, HoodPosition> hoodPositions = new TreeMap<>(config.hoodPositions);
        TreeMap<Double, Double> wheelSpeeds = new TreeMap<>(config.wheelSpeeds);
        HoodPosition hoodPosition = hoodPositions.ceilingEntry(goalDistance).getValue();
        double wheelSpeed = wheelSpeeds.ceilingEntry(goalDistance).getValue();
        turret.setHoodPosition(hoodPosition);
        turret.setTargetWheelSpeed(wheelSpeed);
        if (config.target || config.lights)
            vision.setLedState(true);
        lastFrameNumber = Integer.MIN_VALUE;
        if (config.dataLogging)
            dataCollector.startProvider(getLogDataProvider());
    }

    @Override
    protected boolean step() {
        currentAngle = turret.getAngle();
        vision.setTurretAngle(currentAngle);

        if (config.target) {
            VisionData visionData = vision.getVisionData();
            int frameNumber = visionData.getFrameNumber();
            deltaAngle = visionData.getOffsetAngle();
            newAngle = visionData.getNewAngle();
            if (frameNumber != lastFrameNumber && !Double.isNaN(deltaAngle)) {
                lastFrameNumber = frameNumber;
                turret.setTargetAngle(newAngle);
                logger.info("Moving turret {} degrees. Final angle will be {}", deltaAngle, newAngle);
            }
        }
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        turret.resetWheelSpeed();
        turret.setHoodPosition(HoodPosition.DOWN);
        vision.setLedState(false);
        dataCollector.stopProvider(getLogDataProvider());
    }

    public LogDataProvider getLogDataProvider() {
        if (logDataProvider == null) {
            logDataProvider = new LogDataProvider() {
                @Override
                public String getName() {
                    return TurretPrep.this.getName();
                }

                @Override
                public List<Object> getKeys() {
                    return Arrays.asList("lastFrameNumber", "deltaAngle", "newAngle", "currentAngle");
                }

                @Override
                public List<Object> getValues() {
                    return Arrays.asList(lastFrameNumber, deltaAngle, newAngle, currentAngle);
                }
            };
        }
        return logDataProvider;
    }

    public static class Config {
        public boolean lights = false;
        public boolean target = false;
        public Map<Double, Double> wheelSpeeds;
        public Map<Double, HoodPosition> hoodPositions;
        public boolean dataLogging = false;
    }
}
