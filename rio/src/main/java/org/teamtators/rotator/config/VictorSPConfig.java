package org.teamtators.rotator.config;

import edu.wpi.first.wpilibj.VictorSP;

import java.io.IOException;

public class VictorSPConfig  {
    private int channel;
    private boolean inverted = false;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public VictorSP create() {
        VictorSP victorSP = new VictorSP(channel);
        victorSP.setInverted(inverted);
        return victorSP;
    }
}
