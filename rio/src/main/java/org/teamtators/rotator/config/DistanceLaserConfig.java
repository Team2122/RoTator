package org.teamtators.rotator.config;

import org.teamtators.rotator.components.DistanceLaser;

public class DistanceLaserConfig {

    private int channel;
    private double minDistance;
    private double maxDistance;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    double getMinDistance() {
        return minDistance;
    }

    double getMaxDistance() {
        return maxDistance;
    }

    public DistanceLaser create() {
        DistanceLaser distanceLaser = new DistanceLaser(channel, minDistance, maxDistance);
        return distanceLaser;
    }
}

