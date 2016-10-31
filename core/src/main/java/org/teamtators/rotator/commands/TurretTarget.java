package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.components.*;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.datalogging.LogDataProvider;
import org.teamtators.rotator.subsystems.Turret;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TurretTarget extends CommandBase implements Configurable<TurretTarget.Config> {
    private Config config;
    private ITimeProvider timeProvider;
    private Turret turret;
    private AbstractVision vision;
    private AbstractPicker picker;

    private double startTime;
    private int lastFrameNumber;
    private double deltaAngle;
    private double newAngle;
    private double currentAngle;

    private DataCollector dataCollector;
    private LogDataProvider logDataProvider = null;

    public TurretTarget(CoreRobot robot) {
        super("TurretPrep");
        this.timeProvider = robot.timeProvider();
        this.turret = robot.turret();
        this.vision = robot.vision();
        this.picker = robot.picker();
        this.dataCollector = robot.dataCollector();
        requires(turret);
//        requires(vision);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        if (picker.getPosition() == PickerPosition.HOME) {
            logger.warn("Picker is not out, not targeting");
            cancel();
            return;
        }
        logger.info("Starting targeting with a {} ball", turret.getBallAge());
        vision.setLedState(true);
        lastFrameNumber = Integer.MIN_VALUE;
        if (config.dataLogging)
            dataCollector.startProvider(getLogDataProvider());
        turret.startShooting();
        startTime = timeProvider.getTimestamp();
    }

    @Override
    protected boolean step() {
        BallAge ballAge = turret.getBallAge();
        currentAngle = turret.getAngle();
        vision.setTurretAngle(currentAngle);

        double wheelSpeed = turret.getTargetWheelSpeed();
        if (wheelSpeed == 0) {
            TreeMap<Double, Double> wheelSpeedMap = config.wheelSpeeds.get(ballAge);
            if (wheelSpeedMap != null) {
                Map.Entry<Double, Double> wheelSpeedEntry = wheelSpeedMap.firstEntry();
                if (wheelSpeedEntry != null) {
                    wheelSpeed = wheelSpeedEntry.getValue();
                }
            }
        }
        HoodPosition hoodPosition = turret.getHoodPosition();
        if (hoodPosition == HoodPosition.DOWN) hoodPosition = HoodPosition.UP1;

        VisionData visionData = vision.getVisionData();
        int frameNumber = visionData.getFrameNumber();
        deltaAngle = visionData.getOffsetAngle();
        newAngle = visionData.getNewAngle();
        double goalDistance = visionData.getDistance();

        if (frameNumber != lastFrameNumber
                && !Double.isNaN(deltaAngle)
                && !Double.isNaN(goalDistance)
                && !Double.isNaN(newAngle)) {
            lastFrameNumber = frameNumber;

            if (timeProvider.getTimestamp() >= startTime + config.startDelay) {
                turret.setTargetAngle(newAngle);
                logger.trace("Moving turret {} degrees. Final angle will be {}", deltaAngle, newAngle);
            }

            // look up hood position based on distance to goal and ball age
            hoodPosition = lookupBallEntry(config.hoodPositions, ballAge, goalDistance, hoodPosition);
            wheelSpeed = lookupBallEntry(config.wheelSpeeds, ballAge, goalDistance, wheelSpeed);
        }

        turret.setTargetWheelSpeed(wheelSpeed);
        turret.setHoodPosition(hoodPosition);

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

    private static <T> T lookupBallEntry(Map<BallAge, TreeMap<Double, T>> map, BallAge ballAge, double goalDistance,
                                         T def) {
        TreeMap<Double, T> subMap = map.get(ballAge);
        if (subMap != null && !subMap.isEmpty()) {
            Map.Entry<Double, T> subEntry = subMap.floorEntry(goalDistance);
            if (subEntry != null) {
                return subEntry.getValue();
            } else {
                return subMap.firstEntry().getValue();
            }
        }
        return def;
    }

    public static class Config {
        public double startDelay = 0.5;
        public Map<BallAge, TreeMap<Double, Double>> wheelSpeeds;
        public Map<BallAge, TreeMap<Double, HoodPosition>> hoodPositions;
        public boolean dataLogging = false;
    }
}
