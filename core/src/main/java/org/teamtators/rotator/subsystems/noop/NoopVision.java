package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.subsystems.AbstractVision;
import org.teamtators.rotator.subsystems.VisionData;

public class NoopVision extends AbstractVision {
    @Override
    public VisionData getVisionData() {
        return new VisionData(0, 0, 0);
    }
}
