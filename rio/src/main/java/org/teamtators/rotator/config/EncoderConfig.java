package org.teamtators.rotator.config;

import edu.wpi.first.wpilibj.Encoder;

public class EncoderConfig {
    private int aChannel, bChannel;
    private boolean reverse = false;
    private double distancePerPulse = 1.0;

    public double getDistancePerPulse() {
        return distancePerPulse;
    }

    public void setDistancePerPulse(double distancePerPulse) {
        this.distancePerPulse = distancePerPulse;
    }

    public int getaChannel() {
        return aChannel;
    }

    private void setaChannel(int aChannel) {
        this.aChannel = aChannel;
    }

    public int getbChannel() {
        return bChannel;
    }

    private void setbChannel(int bChannel) {
        this.bChannel = bChannel;
    }

    public boolean isReverse() {
        return reverse;
    }

    private void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public Encoder create() {
        Encoder encoder = new Encoder(aChannel, bChannel, reverse);
        encoder.setDistancePerPulse(distancePerPulse);
        return encoder;
    }
}
