package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.PickerPosition;

/**
 * Picks up a ball
 */
public class PickerPick extends CommandBase implements Configurable<PickerPick.Config> {
    private Config config;
    private AbstractPicker picker;
    private AbstractTurret turret;

    public PickerPick(CoreRobot robot) {
        super("PickerPick");
        this.picker = robot.picker();
        this.turret = robot.turret();
        requires(picker);
        requires(turret);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (!turret.isHomed()) {
            logger.warn("Turret not at home, stopping pick");
            cancel();
            return;
        }
        //Extends the picker
        picker.setPosition(PickerPosition.PICK);
    }

    @Override
    protected boolean step() {
        if (turret.getBallDistance() <= config.maxBallDistance && turret.getBallDistance() >= config.minBallDistance) {
            return true;
        }
        turret.setTargetAngle(0);
        //Starts the rollers
        picker.setPower(config.pickRoller);
        turret.setPinchRollerPower(config.pinchRoller);
        turret.setKingRollerPower(config.kingRoller);
        return false;

    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        picker.resetPower();
        turret.resetPinchRollerPower();
        turret.resetKingRollerPower();
    }

    static class Config {
        public double pickRoller;
        public double pinchRoller;
        public double kingRoller;
        public double minBallDistance = Double.NEGATIVE_INFINITY;
        public double maxBallDistance = Double.POSITIVE_INFINITY;
    }
}
