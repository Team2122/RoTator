package org.teamtators.rotator.commands;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.datalogging.LogDataProvider;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.BallAge;
import org.teamtators.rotator.subsystems.PickerPosition;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Picks up a ball
 */
public class PickerPick extends CommandBase implements Configurable<PickerPick.Config> {
    private final DataCollector dataCollector;
    private final AbstractPicker picker;
    private final AbstractTurret turret;
    private final ScheduledExecutorService executorService;
    private Config config;
    private LogDataProvider logDataProvider = null;

    private SimpleRegression regression = new SimpleRegression();
    private boolean hasSampled = false;

    public PickerPick(CoreRobot robot) {
        super("PickerPick");
        this.picker = robot.picker();
        this.turret = robot.turret();
        dataCollector = robot.dataCollector();
        executorService = robot.executorService();
        requires(picker);
        requires(turret);
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
        hasSampled = false;
        regression.clear();
        if (config.dataLogging)
            dataCollector.startProvider(getLogDataProvider());
    }

    @Override
    protected boolean step() {
        double ballDistance = turret.getBallDistance();
        double compressionSample = turret.getBallCompression();
        if (ballDistance <= config.compression.start && !hasSampled) {
            if (ballDistance <= config.compression.stop) {
                hasSampled = true;
                double a = regression.getSlope(), b = regression.getIntercept();
                double x_1 = config.compression.stop, x_2 = config.compression.start;
                double compression = a * (0.5 * x_2 * x_2 - 0.5 * x_1 * x_1) + b * (x_2 - x_1);
                BallAge ballAge = config.compression.ballAgeThresholds.ceilingEntry(compression).getValue();
                logger.info("Compression sample: {} (R^2={}). Ball is {}", compression,
                        regression.getRSquare(), ballAge);
                turret.setBallAge(ballAge);
            } else {
                regression.addData(ballDistance, compressionSample);
//                logger.debug("added {}: {}", ballDistance, compressionSample);
            }
        }
        double delta = ballDistance - config.targetBallDistance;
        double sign = Math.signum(delta);
        if (Math.abs(delta) <= config.control.stopTolerance) {
            return true;
        } else if (Math.abs(delta) <= config.control.highTolerance) {
            turret.setKingRollerPower(config.control.lowPower * sign);
            picker.resetPower();
            turret.resetPinchRollerPower();
        } else {
            turret.setKingRollerPower(config.control.highPower * sign);
            picker.setPower(config.control.pick * sign);
            turret.setPinchRollerPower(config.control.pinch * sign);
        }
        turret.setTargetAngle(0);
        return false;

    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted)
            super.finish(true);
        else
            logger.info("Finished picking at distance {}, compression value of {}", turret.getBallDistance(),
                    turret.getBallCompression());
        picker.resetPower();
        turret.resetPinchRollerPower();
        turret.resetKingRollerPower();
        executorService.schedule(() -> dataCollector.stopProvider(getLogDataProvider()),
                1000, TimeUnit.MILLISECONDS);
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
        public Compression compression = new Compression();

        public static class Control {
            public double pick, pinch;
            public double highPower, lowPower, highTolerance, stopTolerance;
        }

        public static class Compression {
            public double start, stop;
            public TreeMap<Double, BallAge> ballAgeThresholds;
        }
    }
}
