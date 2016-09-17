package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.*;

public class TurretPrep extends CommandBase implements Configurable<TurretPrep.Config> {
    private Config config;
    private AbstractTurret turret;
    private AbstractVision vision;
    private AbstractPicker picker;

    public TurretPrep(CoreRobot robot) {
        super("TurretPrep");
        this.turret = robot.turret();
        this.vision = robot.vision();
        this.picker = robot.picker();
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
    }

    @Override
    protected boolean step() {
        if (config.target) {
            double deltaAngle = vision.getAngle();
            if (!Double.isNaN(deltaAngle)) {
                double currentAngle = turret.getAngle();
                turret.setTargetAngle(currentAngle + deltaAngle);
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
    }
}
