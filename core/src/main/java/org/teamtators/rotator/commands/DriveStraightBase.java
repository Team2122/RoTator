package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.subsystems.Drive;

/**
 * Created by TatorsDr iverStation on 10/16/2016.
 */
public abstract class DriveStraightBase extends CommandBase {
    protected ControllerFactory controllerFactory;
    protected Drive drive;
    protected double startingDistance;
    protected double deltaDistance;
    protected Config config;
    protected AbstractController controller;

    public DriveStraightBase(String name, CoreRobot robot) {
        super(name);
        this.controllerFactory = robot.controllerFactory();
        this.drive = robot.drive();
    }

    public void configure(Config config) {
        this.config = config;
        controller = controllerFactory.create(config.angleController);
        controller.setName(getName());
        controller.setInputProvider(drive::getGyroAngle);
        controller.setOutputConsumer(output -> {
            drive.setSpeeds(config.speed + output, config.speed - output);
        });
    }

    @Override
    protected void initialize() {
        startingDistance = drive.getAverageDistance();
        controller.enable();
        controller.setSetpoint(config.angle);
    }

    @Override
    public boolean step() {
        deltaDistance = Math.abs(drive.getAverageDistance() - startingDistance);
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        controller.disable();
        drive.resetPowers();
    }

    public static class Config {
        public double angle;
        public double speed;
        public JsonNode angleController;
    }
}
