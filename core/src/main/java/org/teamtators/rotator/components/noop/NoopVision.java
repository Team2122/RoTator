package org.teamtators.rotator.components.noop;

import org.teamtators.rotator.components.AbstractVision;
import org.teamtators.rotator.components.VisionData;

public class NoopVision extends AbstractVision {
    @Override
    public VisionData getVisionData() {
        return new VisionData(0, 0, 0, 0);
    }

    @Override
    public void setTurretAngle(double turretAngle) {

    }
}
