package org.teamtators.rotator.control;

import edu.wpi.first.wpilibj.Timer;

public class WPILibTimeProvider implements ITimeProvider {
    @Override
    public double getTimestamp() {
        return Timer.getFPGATimestamp();
    }
}
