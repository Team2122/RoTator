package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.PIDController;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.HoodPosition;

import javax.inject.Inject;

public class TurretTarget extends CommandBase implements Configurable<TurretTarget.Config> {
    private Config config;
    private AbstractTurret turret;
    private AbstractVision vision;
    private AbstractController controller;

    @Inject
    public TurretTarget(AbstractTurret turret, AbstractVision vision, AbstractController controller) {
        super("TurretTarget");
        requires(turret);
        requires(vision);
        this.turret = turret;
        this.vision = vision;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        vision.setLEDPower(config.LEDPower);
        turret.setHoodPosition(HoodPosition.UP1);
        turret.setTargetWheelSpeed(config.wheelSpeed);
    }

    @Override
    protected boolean step() {
        if(!(turret.isAtLeftLimit() || turret.isAtRightLimit())) {  //Unsure if there is another safety check elsewhere
            //TODO position turret
            vision.getAngle();
        }
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        vision.resetLEDPower();
        turret.resetWheelSpeed();
        turret.setHoodPosition(HoodPosition.DOWN);
    }

    static class Config{
        double LEDPower;
        double wheelSpeed;
    }
}
