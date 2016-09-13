package org.teamtators.rotator.subsystems.noop;

import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.HoodPosition;

public class NoopTurret extends AbstractTurret {
    @Override
    protected void setWheelPower(double power) {

    }

    @Override
    public double getWheelSpeed() {
        return 0;
    }

    @Override
    public HoodPosition getHoodPosition() {
        return null;
    }

    @Override
    public void setHoodPosition(HoodPosition hoodPosition) {

    }

    @Override
    public void setPinchRollerPower(double power) {

    }

    @Override
    public void setKingRollerPower(double power) {

    }

    @Override
    public void setTurretRotation(double power) {

    }

    @Override
    public double getTurretPosition() {
        return 0;
    }

    @Override
    public void resetTurretPosition() {

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
}
