package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.subsystems.Drive;

public class DriveRotate extends CommandBase implements Configurable<DriveRotate.Config> {
    private Config config;
    private Drive drive;
    private ControllerFactory controllerFactory;
    private AbstractController controller;

    public DriveRotate(CoreRobot robot) {
        super("DriveRotate");
        this.drive = robot.drive();
        this.controllerFactory = robot.controllerFactory();
        requires(drive);
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        controller = controllerFactory.create(config.angleController);
        controller.setName(getName());
        controller.setInputProvider(drive::getGyroAngle);
        controller.setOutputConsumer(output -> {
            drive.setLeftSpeed(output);
            drive.setRightSpeed(-output);
        });
    }

    @Override
    protected void initialize() {
        logger.info("Drive rotating from angle {} to {}", drive.getGyroAngle(), config.angle);
        controller.reset();
        controller.setSetpoint(config.angle);
        controller.enable();
    }

    @Override
    protected void finish(boolean interrupted) {
        String logLine = String.format(" at angle %f (target %f)",
                drive.getGyroAngle(), config.angle);
        if (interrupted) {
            logger.warn("Interrupted" + logLine);
        } else {
            logger.info("Finished" + logLine);
        }
        controller.disable();
        drive.resetSpeeds();
    }

    @Override
    protected boolean step() {
        return controller.isOnTarget();
    }

    static class Config {
        public JsonNode angleController;
        public double angle;
    }
}
