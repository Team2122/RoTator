package org.teamtators.rotator.components;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.ControllerPower;

public class DistanceLaser {
    private double minDistance;
    private double maxDistance;
    private AnalogInput distanceLaser;

    public DistanceLaser(AnalogInput distanceLaser, double minDistance, double maxDistance) {
        this.distanceLaser = distanceLaser;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public DistanceLaser(int channel, double minDistance, double maxDistance) {
        this(new AnalogInput(channel), minDistance, maxDistance);
    }

    public double getDistance() {
        double prop = distanceLaser.getVoltage() / ControllerPower.getVoltage5V();
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
