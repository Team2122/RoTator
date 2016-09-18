package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractTurret;

/**
 * Rotate the turret some amount
 */
public class TurretBumpRotation extends CommandBase implements Configurable<TurretBumpRotation.Config> {
    private AbstractTurret turret;
    private Config config;

    public TurretBumpRotation(CoreRobot robot) {
        super("TurretBumpRotation");
        this.turret = robot.turret();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        turret.setTargetAngle(turret.getTargetAngle() + config.angle);
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
