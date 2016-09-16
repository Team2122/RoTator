package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.HoodPosition;
import org.teamtators.rotator.subsystems.PickerPosition;

public class TurretSetHoodPosition extends CommandBase implements Configurable<TurretSetHoodPosition.Config> {
    private AbstractTurret turret;
    private AbstractPicker picker;
    private Config config;

    public TurretSetHoodPosition(CoreRobot robot) {
        super("TurretSetHoodPosition");
        this.turret = robot.turret();
        this.picker = robot.picker();
    }

    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        logger.info("Bringing hood to {} position", config.hoodPosition);
        if (picker.getPosition() == PickerPosition.HOME
                && config.hoodPosition != HoodPosition.DOWN) {
            logger.warn("Tried to bring hood up while picker is not down");
            this.cancel();
        } else {
            turret.setHoodPosition(config.hoodPosition);
        }
    }

    @Override
    protected boolean step() {
        return true;
    }

    @Override
    protected void finish(boolean interrupted) {
    }

    public static class Config {
        public HoodPosition hoodPosition;
    }
}
