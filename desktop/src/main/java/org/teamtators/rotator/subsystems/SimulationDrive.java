package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.Steppable;
import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurable;

public class SimulationDrive extends AbstractDrive implements Configurable<SimulationDrive.Config>, Steppable {
    private Config config;
    private float leftPower = 0;
    private float rightPower = 0;
    private double leftVelocity = 0;
    private double rightVelocity = 0;
    private double leftDistance = 0;
    private double rightDistance = 0;

    @Override
    public void configure(Config config) throws ConfigException {
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
        leftDistance += leftVelocity * delta;
        rightVelocity = rightPower * config.powerToVelocity;
        rightDistance += rightVelocity * delta;
    }

    public static class Config {
        public double powerToVelocity;
    }
}
