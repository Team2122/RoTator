package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.datalogging.LogDataProvider;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.PickerPosition;

/**
 * Picks up a ball
 */
public class PickerShortPick extends CommandBase implements Configurable<PickerShortPick.Config> {
    private final AbstractPicker picker;
    private final AbstractTurret turret;
    private Config config;

    public PickerShortPick(CoreRobot robot) {
        super("PickerShortPick");
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
        picker.setPower(config.pick);
        turret.setPinchRollerPower(config.pinch);
        return turret.getBallDistance() <= config.minBallDistance;

    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        picker.resetPower();
        turret.resetPinchRollerPower();
    }

    public static class Config {
        public double minBallDistance;
        public double pick, pinch;
    }
}
