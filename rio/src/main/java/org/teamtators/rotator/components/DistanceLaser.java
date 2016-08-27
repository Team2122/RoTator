package org.teamtators.rotator.components;

import edu.wpi.first.wpilibj.AnalogInput;

public class DistanceLaser {
    private double minDistance;
    private double maxDistance;

    public DistanceLaser(int channel, double minDistance, double maxDistance) {
        distanceLaser = new AnalogInput(channel);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    private AnalogInput distanceLaser;

    double getDistance() {
        double x = distanceLaser.getVoltage() / 5.0;
        x = (x * (maxDistance - minDistance)) + minDistance;
        return x;
    }

    double getVoltage() {
        return distanceLaser.getVoltage();
    }

}
