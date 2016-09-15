package org.teamtators.rotator.control;

import edu.wpi.first.wpilibj.Timer;

import javax.inject.Inject;

public class WPILibTimeProvider implements ITimeProvider {
    @Inject
    public WPILibTimeProvider() {
    }

    @Override
    public double getTimestamp() {
        return Timer.getFPGATimestamp();
    }
}
