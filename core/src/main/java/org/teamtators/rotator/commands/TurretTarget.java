package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.*;

import javax.inject.Inject;

public class TurretTarget extends CommandBase implements Configurable<TurretTarget.Config> {
    private Config config;
    private AbstractTurret turret;
    private AbstractVision vision;
    private AbstractPicker picker;

    @Inject
    public TurretTarget(AbstractTurret turret, AbstractVision vision, AbstractPicker picker) {
        super("TurretTarget");
        requires(turret);
        requires(vision);
        this.turret = turret;
        this.vision = vision;
        this.picker = picker;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        super.initialize();
        if (picker.getPosition() == PickerPosition.HOME) {
            logger.warn("Picker is not out, not targeting");
            cancel();
            return;
        }
        turret.setHoodPosition(HoodPosition.UP1);
        vision.setLEDPower(config.ledPower);
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

    public static class Config{
        public double ledPower;
        public double wheelSpeed;
    }
}
