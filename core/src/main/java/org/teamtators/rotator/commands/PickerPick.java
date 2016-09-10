package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.PickerPosition;

import javax.inject.Inject;

/**
 * Picks up a ball
 */
public class PickerPick extends CommandBase implements Configurable<PickerPick.Config> {
    static class Config {
        public double pickRollerPower;
        public double pinchRollerPower;
        public double kingRollerPower;
        public double distance;
    }

    private Config config;
    private AbstractPicker picker;
    private AbstractTurret turret;

    @Inject
    public PickerPick(AbstractPicker picker, AbstractTurret turret) {
        super("PickerPick");
        this.picker = picker;
        this.turret = turret;
    }

    public boolean ballPresent() {
        //Checks for if ball is present
        if (turret.getBallDistance() <= config.distance) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        /**
         * if(turretPosition != center) {
         *  return true;
         * }
         */
        super.initialize();
    }

    @Override
    protected boolean step() {
        if (ballPresent()) {
            return true;
        } else {
            //Extends the picker
            picker.setPosition(PickerPosition.PICK);
            //Starts the rollers
            picker.setPower((float) config.pickRollerPower);
            turret.setPinchRollerPower(config.pinchRollerPower);
            turret.setKingRollerPower(config.kingRollerPower);
            return false;
        }
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        picker.resetPower();
        turret.resetPinchRollerPower();
    }
}
