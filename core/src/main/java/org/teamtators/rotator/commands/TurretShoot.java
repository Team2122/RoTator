package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.AbstractVision;
import org.teamtators.rotator.subsystems.HoodPosition;
import org.teamtators.rotator.subsystems.VisionData;

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
        if (turret.getHoodPosition() == HoodPosition.DOWN) {
            logger.warn("Hood currently in down position, not firing.");
            cancel();
            return;
        }
        logger.info("Waiting for shooter to be ready to shoot");
        commandStartTime = timer.getTimestamp();
        rollingStartTime = Double.NaN;
    }

    @Override
    protected boolean step() {
        double timestamp = timer.getTimestamp();
        if (timestamp >= rollingStartTime + config.shootTime) { // if rolling has finished, end command
            return true;
        } else if (!Double.isNaN(rollingStartTime)) { // if rolling has started, continue rolling
            return false;
        } else if (timestamp >= commandStartTime + config.waitTimeout) { // if command has timed out, cancel it
            logger.warn("Command timed out");
            cancel();
            return false;
        }
        // check if roller isn't ready to start yet
        double wheelSpeed = turret.getTargetWheelSpeed();
        if (turret.isAtTargetWheelSpeed() && turret.isAngleOnTarget()
                && wheelSpeed >= config.minShooterWheelSpeed) {
            // start rolling
            rollingStartTime = timestamp;
            double angle = turret.getAngle();
            VisionData visionData = vision.getVisionData();
            double angleOffset = visionData.getOffsetAngle();
            double distance = visionData.getDistance();
            logger.info("Shooting at {} RPM, pointed at {} degrees, vision offset of {}, distance {} in", wheelSpeed,
                    angle, angleOffset, distance);
            turret.setKingRollerPower(config.kingRollerPower);
        }
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted) {
            logger.warn("TurretShoot interrupted");
        } else { // if we successfully shot
            logger.info("TurretShoot ended. Stopping targetting");
            turret.shot();
            turret.setTargetAngle(0);
        }
        turret.resetKingRollerPower();
    }

    static class Config {
        public double kingRollerPower;
        public double waitTimeout;
        public double shootTime;
        public double minShooterWheelSpeed;
    }
}
