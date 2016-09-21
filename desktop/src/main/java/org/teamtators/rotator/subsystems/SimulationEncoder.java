package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.control.Steppable;
import org.teamtators.rotator.config.Configurable;

public class SimulationEncoder implements Steppable, Configurable<SimulationEncoder.Config> {
    private SimulationMotor motor;
    private double rate;
    private double rotations;
    private Config config;

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    public void reset() {
        rate = 0;
        rotations = 0;
    }

    @Override
    public int getExecutionOrder() {
        return 80;
    }

    public SimulationMotor getMotor() {
        return motor;
    }

    public void setMotor(SimulationMotor motor) {
        this.motor = motor;
    }

    public double getRate() {
        return rate * config.multiplier;
    }

    public double getRawRate() {
        return this.rate;
    }

    public void setRawRate(double rate) {
        this.rate = rate;
    }

    public double getDistance() {
        return rotations;
    }

    public void setRotations(double rotations) {
        this.rotations = rotations;
    }

    public void resetRotations() {
        this.setRotations(0);
    }

    @Override
    public void step(double delta) {
        if (motor != null)
            setRawRate(motor.getRate());
        rotations += this.getRate() * delta;
    }

    public static class Config {
        public double multiplier;
    }
}
