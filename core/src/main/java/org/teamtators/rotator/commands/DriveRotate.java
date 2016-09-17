package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.subsystems.AbstractDrive;

public class DriveRotate extends CommandBase implements Configurable<DriveRotate.Config> {
    private Config config;
    private AbstractDrive drive;
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
        controller = controllerFactory.create(config.controller);
        controller.setName("DriveRotate");
        controller.setInputProvider(drive::getGyroAngle);
        controller.setOutputConsumer(output -> {
            drive.setLeftSpeed(config.power + output);
            drive.setRightSpeed(config.power - output);
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
        if (interrupted) {
            logger.warn("Rotation interrupted at angle {} (target {})", drive.getGyroAngle(), config.angle);
        } else {
            logger.info("Rotation completed at angle {} (target {})", drive.getGyroAngle(), config.angle);
        }
        drive.resetSpeeds();
        controller.disable();
    }

    @Override
    protected boolean step() {
        return Math.abs(config.angle - drive.getGyroAngle()) <= config.tolerance;
    }

    static class Config {
        public JsonNode controller;
        public double angle;
        public double tolerance;
        public double power;
    }
}
