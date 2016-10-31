package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.components.AbstractTurret;
import org.teamtators.rotator.subsystems.Turret;

public class TurretBumpWheelSpeedOffset extends CommandBase implements Configurable<TurretBumpWheelSpeedOffset.Config> {
    private final Turret turret;
    private double offset;

    public TurretBumpWheelSpeedOffset(CoreRobot robot) {
        super("TurretBumpWheelSpeedOffset");
        this.turret = robot.turret();
    }

    @Override
    protected void initialize() {
        double newOffset = turret.getWheelSpeedOffset() + offset;
        logger.info("Bumping wheel speed offset by {} RPM to {} RPM", offset, newOffset);
        turret.setWheelSpeedOffset(newOffset);
    }

    @Override
    protected void finish(boolean interrupted) {
    }

    @Override
    protected boolean step() {
        return true;
    }

    @Override
    public void configure(Config config) {
        this.offset = config.offset;
    }

    public static class Config {
        public double offset;
    }
}
