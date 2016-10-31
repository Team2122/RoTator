package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.datalogging.LogDataProvider;
import org.teamtators.rotator.components.AbstractPicker;
import org.teamtators.rotator.components.AbstractTurret;
import org.teamtators.rotator.components.BallAge;
import org.teamtators.rotator.components.PickerPosition;
import org.teamtators.rotator.subsystems.Turret;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Picks up a ball
 */
public class PickerPick extends CommandBase implements Configurable<PickerPick.Config> {
    private final DataCollector dataCollector;
    private final AbstractPicker picker;
    private final Turret turret;
    private Config config;
    private LogDataProvider logDataProvider = null;

    private double maxCompression = 0;

    public PickerPick(CoreRobot robot) {
        super("PickerPick");
        this.picker = robot.picker();
        this.turret = robot.turret();
        dataCollector = robot.dataCollector();
//        requires(picker);
//        requires(turret);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (!turret.isHomed()) {
            logger.warn("Turret not at home, stopping pick");
            cancel();
            return;
        }
        //Extends the picker
        picker.setPosition(PickerPosition.PICK);
        maxCompression = 0.0;
        if (config.dataLogging)
            dataCollector.startProvider(getLogDataProvider());
    }

    @Override
    protected boolean step() {
        double ballDistance = turret.getBallDistance();
        double compressionSample = turret.getBallCompression();
        maxCompression = Math.max(maxCompression, compressionSample);
        double delta = ballDistance - config.targetBallDistance;
        double sign = Math.signum(delta);
        if (Math.abs(delta) <= config.control.stopTolerance) {
            return true;
        } else if (Math.abs(delta) <= config.control.highTolerance) {
            turret.setKingRollerPower(config.control.lowPower * sign);
            picker.resetPickPower();
            picker.resetPinchPower();
        } else {
            turret.setKingRollerPower(config.control.highPower * sign);
            picker.setPickPower(config.control.pick * sign);
            picker.setPinchPower(config.control.pinch * sign);
        }
        turret.setTargetAngle(0);
        return false;

    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted)
            super.finish(true);
        else {
            double compression = maxCompression;
            Map.Entry<Double, BallAge> ballAgeEntry = config.ballAgeThresholds.floorEntry(compression);
            BallAge ballAge = turret.getBallAge();
            if (ballAgeEntry != null) {
                ballAge = ballAgeEntry.getValue();
            } else {
                logger.warn("No ball age entry found for compression {}", compression);
            }
            logger.info("Finished picking at distance {}, compression value of {}, ball age {}",
                    turret.getBallDistance(), compression, ballAge);
            if (compression < 0.2) {
                logger.info("Very little compression, not setting ball type");
            } else {
                turret.setBallAge(ballAge);
            }
        }
        picker.resetPickPower();
        picker.resetPinchPower();
        turret.resetKingRollerPower();
        dataCollector.stopProvider(getLogDataProvider());
    }

    public LogDataProvider getLogDataProvider() {
        if (logDataProvider == null)
            logDataProvider = new LogDataProvider() {
                @Override
                public String getName() {
                    return PickerPick.this.getName();
                }

                @Override
                public List<Object> getKeys() {
                    return Arrays.asList("ballDistance", "ballCompression", "running");
                }

                @Override
                public List<Object> getValues() {
                    return Arrays.asList(turret.getBallDistance(), turret.getBallCompression(), isRunning());
                }
            };
        return logDataProvider;
    }

    public static class Config {
        public double targetBallDistance;
        public boolean dataLogging = false;
        public Control control = new Control();
        public TreeMap<Double, BallAge> ballAgeThresholds;

        public static class Control {
            public double pick, pinch;
            public double highPower, lowPower, highTolerance, stopTolerance;
        }
    }
}
