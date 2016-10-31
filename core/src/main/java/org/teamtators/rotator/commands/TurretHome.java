package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.components.AbstractTurret;
import org.teamtators.rotator.subsystems.Turret;

public class TurretHome extends CommandBase implements Configurable<TurretHome.Config> {
    private Turret turret;

    private double currentSpeed = 0.0;
    private Config config;

    public TurretHome(CoreRobot robot) {
        super("TurretHome");
        this.turret = robot.turret();
        requires(turret);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        currentSpeed = 0.0;
    }

    @Override
    protected boolean step() {
        if (turret.isHomed()) {
            logger.info("Turret has already been homed");
            return true;
        }

        if (turret.homeTurret()) {
            return true;
        } else if (currentSpeed == 0.0) {
            logger.debug("Turret moving right to try to home");
            currentSpeed = config.speed;
        }
        if (turret.isAtRightLimit()) {
            if (currentSpeed > 0) { // if moving right
                logger.debug("Turret hit right limit while homing, switching direction");
                currentSpeed = -config.speed;
            }
        }
        if (turret.isAtLeftLimit()) {
            if (currentSpeed < 0) { // if moving left
                logger.debug("Turret hit left limit while homing, switching direction");
                currentSpeed = config.speed;
            }
        }
        turret.setRotationPower(currentSpeed);
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        turret.resetRotationPower();
    }

    public static class Config {
        public double speed = 0.0;
    }
}
