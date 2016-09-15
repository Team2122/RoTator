package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.HoodPosition;

import javax.inject.Inject;

/**
 * Fires a ball from the turret
 */
public class TurretShoot extends CommandBase implements Configurable<TurretShoot.Config> {
    private Config config;
    private AbstractTurret turret;
    private ITimeProvider timer;
    private double startingTime;

    @Inject
    public TurretShoot(AbstractTurret turret, ITimeProvider timer) {
        super("PickerPick");
        this.turret = turret;
        this.timer = timer;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        startingTime = timer.getTimestamp();
        if (false && !turret.isAtTargetWheelSpeed()) {
            logger.warn("Turret wheel speed not at target (speed: {}, target {}), not firing.",
                    turret.getWheelSpeed(), turret.getTargetWheelSpeed());
            cancel();
        } else if (turret.getHoodPosition() == HoodPosition.DOWN) {
            logger.warn("Hood currently in down position, not firing.");
            cancel();
        } else {
            logger.info("Shooting at {} RPS", turret.getWheelSpeed());
        }
    }

    @Override
    protected boolean step() {
        turret.setKingRollerPower(config.kingRollerPower);
        return timer.getTimestamp() - startingTime > config.timeout;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        turret.resetKingRollerPower();
    }

    static class Config {
        public double kingRollerPower;
        public double timeout;
    }
}
