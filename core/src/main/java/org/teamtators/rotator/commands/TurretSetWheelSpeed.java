package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.components.AbstractTurret;
import org.teamtators.rotator.subsystems.Turret;

public class TurretSetWheelSpeed extends CommandBase implements Configurable<TurretSetWheelSpeed.Config> {
    private Turret turret;
    private Config config;

    public TurretSetWheelSpeed(CoreRobot robot) {
        super("TurretSetWheelSpeed");
        this.turret = robot.turret();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected boolean step() {
        turret.setTargetWheelSpeed(config.speed);
        return true;
    }

    public static class Config {
        public double speed;
    }
}