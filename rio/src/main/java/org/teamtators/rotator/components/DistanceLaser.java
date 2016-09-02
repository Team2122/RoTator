package org.teamtators.rotator.components;

import edu.wpi.first.wpilibj.AnalogInput;

public class DistanceLaser {
    private double minDistance;
    private double maxDistance;
    private AnalogInput distanceLaser;

    public DistanceLaser(int channel, double minDistance, double maxDistance) {
        distanceLaser = new AnalogInput(channel);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public double getDistance() {
        double prop = distanceLaser.getVoltage() / 5.0;
        return (prop * (maxDistance - minDistance)) + minDistance;
    }

    public double getVoltage() {
        return distanceLaser.getVoltage();
    }

    public double getMinDistance() {
        return minDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    AnalogInput getAnalogInput() {
        return distanceLaser;
    }
}
