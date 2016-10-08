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
        double ballDistance = turret.getBallDistance();
        double delta = ballDistance - config.targetBallDistance;
        double sign = Math.signum(delta);
        if (Math.abs(delta) <= config.stopTolerance) {
            return true;
        } else if (Math.abs(delta) <= config.highTolerance) {
            turret.setKingRollerPower(config.lowPower * sign);
            picker.resetPower();
            turret.resetPinchRollerPower();
        } else {
            turret.setKingRollerPower(config.highPower * sign);
            picker.setPower(config.pick * sign);
            turret.setPinchRollerPower(config.pinch * sign);
        }
        turret.setTargetAngle(0);
        return false;

    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        picker.resetPower();
        turret.resetPinchRollerPower();
        turret.resetKingRollerPower();
    }

    public static class Config {
        public double pick, pinch;
        public double highPower, lowPower, highTolerance, stopTolerance;
        public double targetBallDistance;
    }
}
