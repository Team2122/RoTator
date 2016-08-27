package org.teamtators.rotator.components;

import edu.wpi.first.wpilibj.AnalogInput;

public class DistanceLaser {
    public int channel;

    private AnalogInput distanceLaser = new AnalogInput(channel);

    double getDistance(double maxDistance, double minDistance) {
        double x = distanceLaser.getVoltage() / 5.0;
        x = (x * (maxDistance - minDistance)) + minDistance;
        return x;
    }

    double getVoltage() {
        return distanceLaser.getVoltage();
    }

}
