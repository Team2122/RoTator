package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.Steppable;
import org.teamtators.rotator.config.Configurable;

public class SimulationMotor implements Steppable, Configurable<SimulationMotor.Config> {
    private double power;
    private double acceleration;
    private double rate;
    private Config config;

    public SimulationMotor() {
        reset();
    }

    public void reset() {
        power = 0;
        acceleration = 0;
        rate = 0;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    public void step(double delta) {
        double maxRPS = (config.maxRPM / 60.0) / config.gearRatio;
        double pMax = Math.min(Math.abs(rate), maxRPS)  / maxRPS;
        double torque = power * Math.max(1 - pMax, 0) * (config.maxTorque * config.gearRatio);
        acceleration = torque / config.momentum;
        rate += acceleration * delta;
        if (rate > 0)
            rate -= Math.min(rate, config.dampening * delta);
        else
            rate -= Math.max(rate, -config.dampening * delta);
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getRate() {
        return rate;
    }

    public static class Config {
        public double maxTorque;
        public double maxRPM;
        public double gearRatio;
        public double momentum;
        public double dampening;
    }
}
