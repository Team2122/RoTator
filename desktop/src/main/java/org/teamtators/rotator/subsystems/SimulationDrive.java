package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.Steppable;
import org.teamtators.rotator.config.Configurable;

public class SimulationDrive extends AbstractDrive implements Configurable<SimulationDrive.Config>, Steppable {
    private Config config;
    private float leftPower;
    private float rightPower;
    private double leftVelocity;
    private double rightVelocity;
    private double leftDistance;
    private double rightDistance;
    private double x;
    private double y;
    private double rotation;

    public SimulationDrive() {
        reset();
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
        leftPower = 0;
        rightPower = 0;
        leftVelocity = 0;
        rightVelocity = 0;
        leftDistance = 0;
        rightDistance = 0;
        x = 0;
        y = 0;
        rotation = 0;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    public void setLeftPower(float leftPower) {
        this.leftPower = leftPower;
    }

    @Override
    public void setRightPower(float rightPower) {
        this.rightPower = rightPower;
    }

    @Override
    public double getLeftRate() {
        return leftVelocity;
    }

    @Override
    public double getRightRate() {
        return rightVelocity;
    }

    @Override
    public double getLeftDistance() {
        return leftDistance;
    }

    @Override
    public double getRightDistance() {
        return rightDistance;
    }

    @Override
    public void resetEncoders() {
        leftDistance = 0.0;
        rightDistance = 0.0;
    }

    @Override
    public void setDriveMode(DriveMode driveMode) {

    }

    @Override
    public void step(double delta) {
        leftVelocity = leftPower * config.powerToVelocity;
        double dLDist = leftVelocity * delta;
        leftDistance += dLDist;
        rightVelocity = rightPower * config.powerToVelocity;
        double dRDist = rightVelocity * delta;
        rightDistance += dRDist;

        double dRot = (dLDist - dRDist) / (config.wheelWidth);

        rotation += dRot;
        double dist = (dLDist + dRDist) / 2;
        x += Math.cos(rotation) * dist;
        y += Math.sin(rotation) * dist;

        logger.trace("Drive distances (in.): {} {} rates: {} {}", leftDistance, rightDistance, leftVelocity, rightVelocity);
        logger.trace("Drive position {} {} rotation {}", x, y, rotation);
    }

    public static class Config {
        public double powerToVelocity;
        public double wheelWidth;
    }
}
