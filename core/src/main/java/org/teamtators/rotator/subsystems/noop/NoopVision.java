package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.subsystems.AbstractVision;

public class NoopVision extends AbstractVision {
    @Override
    public double getDistance() {
        return 0;
    }

    @Override
    public double getAngle() {
        return 0;
    }
}
