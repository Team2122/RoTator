package org.teamtators.rotator.commands;

import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractPicker;

public class DriveStraightCheval extends DriveStraightBase implements Configurable<DriveStraightBase.Config> {
    private AbstractPicker picker;

    public DriveStraightCheval(CoreRobot robot) {
        super("DriveStraightCheval", robot);
        this.picker = robot.picker();
        requires(picker);
    }

    @Override
    protected void initialize() {
        super.initialize();
        logger.info("Driving straight until cheval");
    }

    @Override
    public boolean step() {
        return picker.isAtCheval();
    }

    @Override
    protected void finish(boolean interrupted) {
        super.finish(interrupted);
        if (interrupted)
            logger.warn("DriveStraightCheval interrupted");
        else
            logger.info("Drove until hit cheval sensor");
    }
}
