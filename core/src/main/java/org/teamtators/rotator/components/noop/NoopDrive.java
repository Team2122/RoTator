package org.teamtators.rotator.components.noop;

import org.teamtators.rotator.components.Gyro;
import org.teamtators.rotator.components.SimulationGyro;
import org.teamtators.rotator.components.AbstractDrive;
import org.teamtators.rotator.tester.ComponentTestGroup;

/**
 * drive implementation which does nothing
 */
public class NoopDrive extends AbstractDrive {
    private SimulationGyro simulationGyro = new SimulationGyro();

    public NoopDrive() {
    }

    @Override
    public void setLeftPower(double leftPower) {

    }

    @Override
    public void setRightPower(double rightPower) {

    }

    @Override
    public double getLeftRate() {
        return 0;
    }

    @Override
    public double getRightRate() {
        return 0;
    }

    @Override
    public double getLeftDistance() {
        return 0;
    }

    @Override
    public double getRightDistance() {
        return 0;
    }

    @Override
    public void resetEncoders() {

    }

    @Override
    public Gyro getGyro() {
        return simulationGyro;
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        return null;
    }
}
