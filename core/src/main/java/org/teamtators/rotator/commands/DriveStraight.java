package org.teamtators.rotator.commands;

import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.control.TrapezoidController;

/**
 * Drive in a straight line for a certain distance
 */
public class DriveStraight extends DriveStraightBase implements Configurable<DriveStraight.Config> {
    private Config config;
    private TrapezoidController speedController;

    public DriveStraight(CoreRobot robot) {
        super("DriveStraight", robot);
        requires(drive);
    }

    @Override
    public void configure(Config config) {
        super.configure(config);
        this.config = config;
        speedController = controllerFactory.createTrapezoidController();
        speedController.setName(getName() + "Speed");
        speedController.setExecutionOrder(190); // so it runs before the angle controller
        config.speedController.travelVelocity = config.speed;
        speedController.configure(config.speedController);
        speedController.setInputProvider(this::getDeltaDistance);
        speedController.setOutputConsumer(output -> this.speed = output);
    }

    @Override
    protected void initialize() {
        logger.info("Driving at angle {} (currently at {}) for distance of {}",
                config.angle, drive.getGyroAngle(), config.distance);
        speedController.setStartVelocity(drive.getAverageRate());
        speedController.setSetpoint(config.distance);
        speedController.enable();
        super.initialize();
    }

    @Override
    public boolean step() {
        super.step();
        return speedController.isOnTarget();
    }

    @Override
    public void finish(boolean interrupted) {
        super.finish(interrupted);
        speedController.disable();
        String logString = String.format(" at distance %s (target %s), angle %s (target %s)",
                getDeltaDistance(), config.distance, drive.getGyroAngle(), config.angle);
        if (interrupted) {
            logger.warn("Interrupted" + logString);
        } else {
            logger.info("Finishing" + logString);
        }
    }

    public static class Config extends DriveStraightBase.Config {
        public double distance;
        public double speed;
        public TrapezoidController.Config speedController;
    }
}
