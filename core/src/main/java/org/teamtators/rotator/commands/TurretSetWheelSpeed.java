package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractTurret;

import javax.inject.Inject;

public class TurretSetWheelSpeed extends CommandBase implements Configurable<TurretSetWheelSpeed.Config> {
    private AbstractTurret turret;
    private Config config;

    @Inject
    public TurretSetWheelSpeed(AbstractTurret turret) {
        super("TurretSetWheelSpeed");
        this.turret = turret;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected boolean step() {
        turret.setWheelSpeed(config.speed);
        return true;
    }

    public static class Config {
        public double speed;
    }
}