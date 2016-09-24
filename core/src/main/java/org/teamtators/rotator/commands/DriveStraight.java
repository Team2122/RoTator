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
public class DriveStraight extends CommandBase implements Configurable<DriveStraight.Config> {
    private Config config;
    private ControllerFactory controllerFactory;
    private AbstractDrive drive;
    private AbstractController controller;
    private double startingDistance;

    public DriveStraight(CoreRobot robot) {
        super("DriveStraight");
        this.drive = robot.drive();
        requires(drive);
        this.controllerFactory = robot.controllerFactory();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        controller = controllerFactory.create(config.angleController);
        controller.setInputProvider(drive::getGyroAngle);
        controller.setOutputConsumer(output -> {
            drive.setLeftSpeed(config.power - output);
            drive.setRightSpeed(config.power + output);
        });
    }

    @Override
    protected void initialize() {
        logger.info("Driving at angle {} (currently at {}) for distance of {}",
                config.targetAngle, drive.getGyroAngle(), config.distance);
        startingDistance = drive.getAverageDistance();
        controller.enable();
        controller.setSetpoint(config.targetAngle);
    }

    @Override
    public boolean step() {
        return Math.abs(drive.getAverageDistance() - startingDistance) > config.distance;
    }

    @Override
    protected void finish(boolean interrupted) {
        String logString = String.format(interrupted ? "Interrupted" : "Finishing" + " at distance %s (target %s), angle %s (target %s)",
                Math.abs(drive.getAverageDistance() - startingDistance), config.distance, drive.getGyroAngle(), config.targetAngle);
        if (interrupted) {
            logger.warn(logString);
        } else {
            logger.info(logString);
        }
        controller.disable();
        drive.resetSpeeds();
    }

    static class Config {
        public double targetAngle;
        public double power;
        public double distance;
        public JsonNode angleController;
    }
}
