package org.teamtators.rotator.commands;

import com.google.inject.Inject;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.HoodPosition;
import org.teamtators.rotator.subsystems.PickerPosition;

public class HoodSetPosition extends CommandBase implements Configurable<HoodSetPosition.Config> {
    private AbstractTurret turret;
    private AbstractPicker picker;
    private Config config;

    @Inject
    public HoodSetPosition(AbstractTurret turret, AbstractPicker picker) {
        super("HoodSetPosition");
        this.turret = turret;
        this.picker = picker;
    }

    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (picker.getPosition() == null
                || picker.getPosition() == PickerPosition.HOME
                || config.hoodPosition != HoodPosition.DOWN) {
            logger.warn("Tried to bring hood up while picker is not down");
            this.cancel();
        }
    }

    @Override
    protected boolean step() {
        turret.setHoodPosition(config.hoodPosition);
        return true;
    }

    @Override
    protected void finish(boolean interrupted) {
    }

    public static class Config {
        public HoodPosition hoodPosition;
    }
}
