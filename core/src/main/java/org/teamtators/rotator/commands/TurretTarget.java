package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.datalogging.LogDataProvider;
import org.teamtators.rotator.subsystems.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TurretTarget extends CommandBase implements Configurable<TurretTarget.Config> {
    private Config config;
    private AbstractTurret turret;
    private AbstractVision vision;
    private AbstractPicker picker;

    private int lastFrameNumber;
    private double deltaAngle;
    private double newAngle;
    private double currentAngle;

    private DataCollector dataCollector;
    private LogDataProvider logDataProvider = null;

    public TurretTarget(CoreRobot robot) {
        super("TurretPrep");
        this.turret = robot.turret();
        this.vision = robot.vision();
        this.picker = robot.picker();
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
        if (config.lights) vision.setLedState(true);
        lastFrameNumber = Integer.MIN_VALUE;
        if (config.dataLogging)
            dataCollector.startProvider(getLogDataProvider());
        turret.startShooting();
    }

    @Override
    protected boolean step() {
        BallAge ballAge = turret.getBallAge();
        currentAngle = turret.getAngle();
        vision.setTurretAngle(currentAngle);

        double goalDistance = 0;
        VisionData visionData = vision.getVisionData();
        int frameNumber = visionData.getFrameNumber();
        deltaAngle = visionData.getOffsetAngle();
        newAngle = visionData.getNewAngle();
        goalDistance = vision.getVisionData().getDistance();
        if (frameNumber != lastFrameNumber && !Double.isNaN(deltaAngle) &&
                !Double.isNaN(goalDistance) && !Double.isNaN(newAngle)) {
            lastFrameNumber = frameNumber;
            turret.setTargetAngle(newAngle);
            logger.trace("Moving turret {} degrees. Final angle will be {}", deltaAngle, newAngle);

            // look up hood position based on distance to goal and ball age
            HoodPosition hoodPosition = config.defaultHoodPosition;
            TreeMap<Double, HoodPosition> hoodPositionsMap = config.hoodPositions.get(ballAge);
            if (hoodPositionsMap.isEmpty()) {
                logger.warn("No hood position specified for ball age of {}", ballAge);
            } else {
                hoodPosition = hoodPositionsMap.floorEntry(goalDistance).getValue();
            }
            turret.setHoodPosition(hoodPosition);
        }

        // look up wheel speed based on goal distance and ball age
        double wheelSpeed = turret.getTargetWheelSpeed();
        TreeMap<Double, Double> wheelSpeedsMap = config.wheelSpeeds.get(ballAge);
        if (!Double.isNaN(goalDistance)) {
            if (wheelSpeedsMap.isEmpty()) {
                logger.warn("No wheel speed specified for ball age of {}", ballAge);
            } else {
                wheelSpeed = wheelSpeedsMap.floorEntry(goalDistance).getValue();
            }
        }
        turret.setTargetWheelSpeed(wheelSpeed);
        return turret.hasShot();
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
                    return TurretTarget.this.getName();
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
        public Map<BallAge, TreeMap<Double, Double>> wheelSpeeds;
        public Map<BallAge, TreeMap<Double, HoodPosition>> hoodPositions;
        public HoodPosition defaultHoodPosition;
        public boolean dataLogging = false;
    }
}
