package org.teamtators.rotator.components.noop;

import org.teamtators.rotator.components.AbstractTurret;
import org.teamtators.rotator.components.HoodPosition;
import org.teamtators.rotator.tester.ComponentTestGroup;

public class NoopTurret extends AbstractTurret {
    @Override
    public void setWheelPower(double power) {

    }

    @Override
    public double getWheelRate() {
        return 0;
    }

    @Override
    public double getWheelRotations() {
        return 0;
    }

    @Override
    public void resetWheelRotations() {

    }

    @Override
    public HoodPosition getHoodPosition() {
        return null;
    }

    @Override
    public void setHoodPosition(HoodPosition hoodPosition) {

    }

    @Override
    public void setKingRollerPower(double power) {

    }

    @Override
    public void setRotationPower(double power) {

    }

    @Override
    public double getAngle() {
        return 0;
    }

    @Override
    public void resetAngleEncoder() {

    }

    @Override
    public boolean isAtLeftLimit() {
        return false;
    }

    @Override
    public boolean isAtRightLimit() {
        return false;
    }

    @Override
    public boolean isAtCenterLimit() {
        return false;
    }

    @Override
    public double getBallDistance() {
        return 0;
    }

    @Override
    public double getBallCompression() {
        return 0;
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        return super.getTestGroup();
    }
}
