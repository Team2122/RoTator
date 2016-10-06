package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.PickerPosition;

/**
 * Barfs a ball in either direction
 */
public class PickerBarf extends CommandBase implements Configurable<PickerBarf.Config> {
    private Config config;
    private AbstractPicker picker;
    private AbstractTurret turret;

    public PickerBarf(CoreRobot robot) {
        super("PickerBarf");
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
    }

    @Override
    protected boolean step() {
        picker.setPower(config.pick);
        turret.setPinchRollerPower(config.pinch);
        turret.setKingRollerPower(config.king);
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
        public double pick, pinch, king;
    }
}
