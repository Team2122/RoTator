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
    private double startingTime;

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
        startingTime = timer.getTimestamp();
        double wheelSpeed = turret.getWheelSpeed();
        if (!turret.isAtTargetWheelSpeed()) {
            logger.warn("Turret wheel speed not at target (speed: {}, target {}), not firing.",
                    wheelSpeed, turret.getTargetWheelSpeed());
            cancel();
        } else if (turret.getHoodPosition() == HoodPosition.DOWN) {
            logger.warn("Hood currently in down position, not firing.");
            cancel();
        } else {
            double angle = turret.getAngle();
            double angleOffset = vision.getAngle();
            logger.info("Shooting at {} RPS, pointed at {} degrees, vision offset of {}", wheelSpeed, angle,
                    angleOffset);
            turret.setKingRollerPower(config.kingRollerPower);
        }
    }

    @Override
    protected boolean step() {
        return timer.getTimestamp() - startingTime > config.timeout;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        turret.resetKingRollerPower();
        if (!interrupted) { // if we successfully shot
            takeRequirements(turret); // cancel everything else using turret (TurretPrep)
        }
    }

    static class Config {
        public double kingRollerPower;
        public double timeout;
    }
}
