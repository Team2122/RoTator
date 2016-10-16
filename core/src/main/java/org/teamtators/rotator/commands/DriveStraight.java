package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.subsystems.AbstractDrive;

/**
 * Drive in a straight line for a certain distance
 */
public class DriveStraight extends DriveStraightBase implements Configurable<DriveStraight.Config> {
    private Config config;

    public DriveStraight(CoreRobot robot) {
        super("DriveStraight", robot);
        requires(drive);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        super.configure(config);
    }
    @Override
    protected void initialize() {
        super.initialize();
        logger.info("Driving at angle {} (currently at {}) for distance of {}",
                config.angle, drive.getGyroAngle(), config.distance);
    }

    @Override
    public boolean step() {
        super.step();
        return Math.abs(deltaDistance) >= config.distance;
    }

    @Override
    public void finish(boolean interrupted) {
        super.finish(interrupted);
        String logString = String.format(" at distance %s (target %s), angle %s (target %s)",
                deltaDistance, config.distance, drive.getGyroAngle(), config.angle);
        if (interrupted) {
            logger.warn("Interrupted" + logString);
        } else {
            logger.info("Finishing" + logString);
        }
    }

    public class Config extends DriveStraightBase.Config {
        public double distance;
    }

}
