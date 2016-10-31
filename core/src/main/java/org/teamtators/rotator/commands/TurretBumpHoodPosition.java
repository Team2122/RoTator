package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.components.AbstractPicker;
import org.teamtators.rotator.components.AbstractTurret;
import org.teamtators.rotator.components.HoodPosition;
import org.teamtators.rotator.components.PickerPosition;
import org.teamtators.rotator.subsystems.Turret;

public class TurretBumpHoodPosition extends CommandBase implements Configurable<TurretBumpHoodPosition.Config> {
    private Turret turret;
    private AbstractPicker picker;
    private boolean bumpUp;

    public TurretBumpHoodPosition(CoreRobot robot) {
        super("TurretBumpHoodPosition");
        this.turret = robot.turret();
        this.picker = robot.picker();
    }

    public void configure(Config config) {
        bumpUp = config.direction.equalsIgnoreCase("up");
    }

    @Override
    protected void initialize() {
        HoodPosition position = turret.getHoodPosition();
        HoodPosition[] values = HoodPosition.values();
        if (bumpUp) {
            if (position.ordinal() == values.length - 1) {
                logger.info("Hood already all the way up");
                return;
            } else {
                position = values[position.ordinal() + 1];
            }
        } else {
            if (position.ordinal() == 0) {
                logger.info("Hood already all the way down");
                return;
            } else {
                position = values[position.ordinal() - 1];
            }
        }
        if (picker.getPosition() == PickerPosition.HOME
                && position != HoodPosition.DOWN) {
            logger.warn("Tried to bring hood up while picker is not down");
            this.cancel();
        } else {
            logger.info("Bumping hood {} to {}", bumpUp ? "up" : "down", position);
            turret.setHoodPosition(position);
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
        public String direction;
    }
}
