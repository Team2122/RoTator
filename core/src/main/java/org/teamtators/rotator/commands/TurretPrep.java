package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.*;

public class TurretPrep extends CommandBase implements Configurable<TurretPrep.Config> {
    private Config config;
    private AbstractTurret turret;
    private AbstractPicker picker;
    private AbstractVision vision;

    public TurretPrep(CoreRobot robot) {
        super("TurretPrep");
        this.turret = robot.turret();
        this.picker = robot.picker();
        this.vision = robot.vision();
        requires(turret);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (picker.getPosition() == PickerPosition.HOME) {
            logger.warn("Picker is in the way, canceling");
            cancel();
        }
        turret.startShooting();
    }

    @Override
    protected boolean step() {
        if (config.lights) vision.setLedState(true);
        turret.setHoodPosition(config.hoodPosition);
        turret.setTargetWheelSpeed(config.wheelSpeed);
        return turret.hasShot();
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        turret.resetWheelSpeed();
        turret.setHoodPosition(HoodPosition.DOWN);
        vision.setLedState(false);
    }

    public static class Config {
        public boolean lights = false;
        public double wheelSpeed;
        public HoodPosition hoodPosition = HoodPosition.UP1;
    }
}
