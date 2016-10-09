package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractTurret;

/**
 * Rotate the turret to an angle
 */
public class TurretSetRotation extends CommandBase implements Configurable<TurretSetRotation.Config> {
    private AbstractTurret turret;
    private Config config;

    public TurretSetRotation(CoreRobot robot) {
        super("TurretBumpRotation");
        this.turret = robot.turret();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        logger.info("Rotating turret to {} degrees", config.angle);
        turret.setTargetAngle(config.angle);
    }

    @Override
    protected boolean step() {
        return true;
    }

    @Override
    protected void finish(boolean interrupted) {
    }

    static class Config {
        public double angle;
    }
}
