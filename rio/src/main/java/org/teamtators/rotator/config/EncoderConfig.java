package org.teamtators.rotator.config;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;

public class EncoderConfig {
    private int aChannel, bChannel;
    private boolean reverse = false;
    private double distancePerPulse = 1.0;
    private CounterBase.EncodingType encodingType = CounterBase.EncodingType.k4X;
    private int samplesToAverage = 0;

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

    public void setbChannel(int bChannel) {
        this.bChannel = bChannel;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public CounterBase.EncodingType getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(CounterBase.EncodingType encodingType) {
        this.encodingType = encodingType;
    }

    public int getSamplesToAverage() {
        return samplesToAverage;
    }

    public void setSamplesToAverage(int samplesToAverage) {
        this.samplesToAverage = samplesToAverage;
    }

    public Encoder create() {
        Encoder encoder = new Encoder(aChannel, bChannel, reverse, encodingType);
        encoder.setDistancePerPulse(distancePerPulse);
        if (samplesToAverage >= 1 && samplesToAverage <= 127 )
            encoder.setSamplesToAverage(samplesToAverage);
        return encoder;
    }
}
