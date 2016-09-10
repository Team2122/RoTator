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

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        picker.setPosition(PickerPosition.PICK);
    }

    @Override
    protected boolean step() {
        picker.setPower((float) config.pickRollerPower);
        turret.setPinchRollerPower(config.pinchRollerPower);
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        picker.resetPower();
        turret.resetPinchRollerPower();
    }
}
