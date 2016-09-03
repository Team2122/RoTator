package org.teamtators.rotator.control;

import edu.wpi.first.wpilibj.Timer;

public class WPILibTimeProvider implements ITimeProvider {
    @Override
    public long currentTimeMillis() {
        return (long) (Timer.getFPGATimestamp()*1000);
    }
}
