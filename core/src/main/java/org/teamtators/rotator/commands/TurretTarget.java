package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractTurret;

import javax.inject.Inject;

public class TurretTarget extends CommandBase implements Configurable<TurretTarget.Config> {
    private Config config;
    private AbstractTurret turret;

    @Inject
    public TurretTarget(AbstractTurret turret) {
        super("TurretTarget");
        requires(turret);
        this.turret = turret;
    }

    @Override
    public void configure(Config config) {

        if(!(turret.isAtLeftLimit() || turret.isAtRightLimit())) {
            //TODO position turret
        }
    }

    @Override
    protected boolean step() {
        return false;
    }


    static class Config{

    }
}
