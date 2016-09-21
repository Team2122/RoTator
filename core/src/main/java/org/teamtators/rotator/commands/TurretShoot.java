package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.AbstractVision;
import org.teamtators.rotator.subsystems.HoodPosition;

/**
 * Fires a ball from the turret
 */
public class TurretShoot extends CommandBase implements Configurable<TurretShoot.Config> {
    private Config config;
    private AbstractTurret turret;
    private AbstractVision vision;
    private ITimeProvider timer;
    private double commandStartTime = Double.NaN;
    private double rollingStartTime = Double.NaN;

    public TurretShoot(CoreRobot robot) {
        super("PickerPick");
        this.turret = robot.turret();
        this.vision = robot.vision();
        this.timer = robot.timeProvider();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        commandStartTime = timer.getTimestamp();
    }

    @Override
    protected boolean step() {
        double timestamp = timer.getTimestamp();
        if (timestamp - rollingStartTime > config.shootTime) { // if rolling has finished, end command
            return true;
        } else if (rollingStartTime != Double.NaN) { // if rolling has staIrted, continue rolling
            return false;
        } else if (timestamp - commandStartTime > config.waitTimeout) { // if command has timed out, cancel it
            logger.warn("Command timed out");
            cancel();
            return false;
        }
        // check if roller isn't ready to start yet
        double wheelSpeed = turret.getWheelSpeed();
        if (!turret.isAtTargetWheelSpeed()) {
            logger.trace("Turret wheel speed not at target (speed: {}, target {}), not firing.",
                    wheelSpeed, turret.getTargetWheelSpeed());
        } else if (turret.getHoodPosition() == HoodPosition.DOWN) {
            logger.trace("Hood currently in down position, not firing.");
        } else if (!turret.isAngleOnTarget()) {
            logger.trace("Turret angle is not on target, not firing");
        } else {
            // start rolling
            rollingStartTime = timestamp;
            double angle = turret.getAngle();
            double angleOffset = vision.getAngle();
            logger.info("Shooting at {} RPS, pointed at {} degrees, vision offset of {}", wheelSpeed, angle,
                    angleOffset);
            turret.setKingRollerPower(config.kingRollerPower);
        }
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        turret.resetKingRollerPower();
        if (!interrupted) { // if we successfully shot
            takeRequirements(turret); // cancel everything else using turret (TurretPrep)
        }
        turret.setTargetAngle(0);
    }

    static class Config {
        public double kingRollerPower;
        public double waitTimeout;
        public double shootTime;
    }
}
