package org.teamtators.rotator.subsystems;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Singleton;
import org.teamtators.rotator.components.Gyro;
import org.teamtators.rotator.components.SimulationGyro;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.Steppable;
import org.teamtators.rotator.config.Configurable;

import javax.inject.Inject;

@Singleton
public class SimulationDrive extends AbstractDrive implements Configurable<SimulationDrive.Config>, Steppable {
    private Config config;
    private SimulationMotor leftMotor = new SimulationMotor();
    private SimulationMotor rightMotor = new SimulationMotor();
    private SimulationEncoder leftEncoder = new SimulationEncoder();
    private SimulationEncoder rightEncoder = new SimulationEncoder();
    private double maxX;
    private double maxY;
    private double x;
    private double y;
    private double rotation;
    private SimulationGyro gyro = new SimulationGyro();

    public SimulationDrive() {
        reset();
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void reset() {
        leftMotor.reset();
        rightMotor.reset();
        x = 0;
        y = 0;
        rotation = 0;
    }

    @Inject
    private ControllerFactory controllerFactory;

    @Override
    public void configure(Config config) {
        this.config = config;
        leftMotor.configure(config.motor);
        rightMotor.configure(config.motor);
        leftEncoder.configure(config.encoder);
        rightEncoder.configure(config.encoder);
        setMaxSpeed(config.maxSpeed);
        setLeftController(controllerFactory.create(config.controller));
        setRightController(controllerFactory.create(config.controller));
        setDriveMode(config.driveMode);
    }

    @Override
    public void setLeftPower(double leftPower) {
        this.leftMotor.setPower(leftPower);
    }

    @Override
    public void setRightPower(double rightPower) {
        this.rightMotor.setPower(rightPower);
    }

    @Override
    public double getLeftRate() {
        return this.leftEncoder.getRate();
    }

    @Override
    public double getRightRate() {
        return this.rightEncoder.getRate();
    }

    @Override
    public double getLeftDistance() {
        return this.leftEncoder.getDistance();
    }

    @Override
    public double getRightDistance() {
        return this.rightEncoder.getDistance();
    }

    @Override
    public void resetEncoders() {
        leftEncoder.resetRotations();
        rightEncoder.resetRotations();
    }

    @Override
    public Gyro getGyro() {
        return gyro;
    }

    @Override
    public void step(double delta) {
        leftMotor.step(delta);
        rightMotor.step(delta);

        double leftRate = leftMotor.getRate();
        double rightRate = rightMotor.getRate();
        double dRate = leftRate - rightRate;
        double scrub = dRate * Math.pow(config.scrubCoef, 1 / delta);
        leftRate -= scrub;
        rightRate += scrub;

        leftEncoder.setRawRate(leftRate);
        rightEncoder.setRawRate(rightRate);
        leftEncoder.step(delta);
        rightEncoder.step(delta);

        double dLDist = leftEncoder.getRate() * delta;
        double dRDist = rightEncoder.getRate() * delta;

        double dRot = (dLDist - dRDist) / (config.wheelWidth);

        rotation += dRot;

        gyro.setRate(Math.toDegrees(dRot) / delta);
        gyro.setAngle(Math.toDegrees(rotation));

        double dist = (dLDist + dRDist) / 2;
        x += Math.cos(rotation) * dist;
        y += Math.sin(rotation) * dist;

        if (x < 0) {
            x = 0;
            rotation = Math.round(rotation / (.5 * Math.PI)) * .5 * Math.PI;
        }
        if (x > maxX)
            x = maxX;
        if (y < 0)
            y = 0;
        if (y > maxY)
            y = maxY;
    }

    public int getWidth() {
        return config.width;
    }

    public int getLength() {
        return config.length;
    }

    public static class Config {
        public double wheelWidth;
        public double scrubCoef;
        public int width;
        public int length;
        public SimulationMotor.Config motor;
        public SimulationEncoder.Config encoder;
        public DriveMode driveMode;
        public JsonNode controller;
        public double maxSpeed;
    }
}
