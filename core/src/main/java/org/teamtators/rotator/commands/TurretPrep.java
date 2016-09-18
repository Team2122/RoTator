package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.subsystems.*;

public class TurretPrep extends CommandBase implements Configurable<TurretPrep.Config> {
    private Config config;
    private AbstractTurret turret;
    private AbstractVision vision;
    private AbstractPicker picker;
    private ITimeProvider timer;
    private double targetStart;

    public TurretPrep(CoreRobot robot) {
        super("TurretPrep");
        this.turret = robot.turret();
        this.vision = robot.vision();
        this.picker = robot.picker();
        this.timer = robot.timeProvider();
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
        turret.setHoodPosition(HoodPosition.UP1);
        turret.setTargetWheelSpeed(config.wheelSpeed);
        if (config.target)
            vision.setLedState(true);
        targetStart = Double.NEGATIVE_INFINITY;
    }

    @Override
    protected boolean step() {
        if (config.target) {
            double deltaAngle = vision.getAngle();
            double now = timer.getTimestamp();
            if (turret.isAngleOnTarget() && Double.isNaN(targetStart)) {
                targetStart = now;
                logger.debug("Waiting for vision to stabilize for {} s", config.target);
            }
            if (!Double.isNaN(deltaAngle) && now >= targetStart + config.targetDelay) {
                double currentAngle = turret.getAngle();
                double newAngle = currentAngle + deltaAngle;
                turret.setTargetAngle(newAngle);
                logger.info("Moving turret {} degrees. Final angle will be {}", deltaAngle, newAngle);
                targetStart = Double.NaN;
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
    }

    public static class Config {
        public boolean target = false;
        public double wheelSpeed;
        public double targetDelay;
    }
}
