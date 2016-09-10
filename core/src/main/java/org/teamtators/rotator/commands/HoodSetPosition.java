package org.teamtators.rotator.commands;

import com.google.inject.Inject;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.HoodPosition;

public class HoodSetPosition extends CommandBase {
    private AbstractTurret turret;

    @Inject
    public HoodSetPosition(AbstractTurret turret) {
        super("HoodSetPosition");
        this.turret = turret;
    }

    public static class Config {
        public HoodPosition hoodPosition;
    }

    public HoodPosition hoodPosition;

    public void configure(Config config) {
        this.hoodPosition = hoodPosition;
    }

    @Override
    protected boolean step() {
        turret.setHoodPosition(hoodPosition);
        return true;
    }
}
}
