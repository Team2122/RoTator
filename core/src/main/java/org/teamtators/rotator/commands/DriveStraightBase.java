package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.subsystems.AbstractDrive;

/**
 * Created by TatorsDriverStation on 10/16/2016.
 */
public abstract class DriveStraightBase extends CommandBase {
    protected ControllerFactory controllerFactory;
    protected AbstractDrive drive;
    protected double startingDistance;
    protected Config config;
    protected AbstractController controller;
    protected double speed;

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
            drive.setLeftSpeed(speed + output);
            drive.setRightSpeed(speed - output);
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
        return false;
    }

    protected double getDeltaDistance() {
        return Math.abs(drive.getAverageDistance() - startingDistance);
    }

    @Override
    protected void finish(boolean interrupted) {
        controller.disable();
        drive.resetSpeeds();
    }

    public static class Config {
        public double angle;
        public JsonNode angleController;
    }
}
