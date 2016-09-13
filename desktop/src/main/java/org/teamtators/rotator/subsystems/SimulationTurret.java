package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Steppable;

public class SimulationTurret extends AbstractTurret implements Steppable, Configurable<SimulationTurret.Config> {
    private SimulationMotor shooterWheelMotor = new SimulationMotor();
    private SimulationEncoder shooterWheelEncoder = new SimulationEncoder();
    private SimulationMotor rotationMotor = new SimulationMotor();
    private SimulationEncoder rotationEncoder = new SimulationEncoder();
    private SimulationMotor kingRollerMotor = new SimulationMotor();
    private SimulationEncoder kingRollerEncoder = new SimulationEncoder();
    private SimulationMotor pinchRollerMotor = new SimulationMotor();
    private SimulationEncoder pinchRollerEncoder = new SimulationEncoder();
    private double ballDistance;

    public SimulationTurret() {
        shooterWheelEncoder.setMotor(shooterWheelMotor);
        rotationEncoder.setMotor(rotationMotor);
        kingRollerEncoder.setMotor(kingRollerMotor);
        pinchRollerEncoder.setMotor(pinchRollerMotor);
    }

    @Override
    public void step(double delta) {
        shooterWheelMotor.step(delta);
        shooterWheelEncoder.step(delta);
        rotationMotor.step(delta);
        rotationEncoder.step(delta);
        kingRollerMotor.step(delta);
        kingRollerEncoder.step(delta);
        pinchRollerMotor.step(delta);
        pinchRollerEncoder.step(delta);
    }

    @Override
    protected void setWheelPower(double power) {
        shooterWheelMotor.setPower(power);
    }

    @Override
    public double getWheelSpeed() {
        return shooterWheelEncoder.getRate();
    }

    @Override
    public void setPinchRollerPower(double power) {
        pinchRollerMotor.setPower(power);
    }

    @Override
    public void setKingRollerPower(double power) {
        kingRollerMotor.setPower(power);
    }

    @Override
    public void setTurretRotation(double power) {
        rotationMotor.setPower(power);
    }

    @Override
    public double getTurretPosition() {
        return rotationEncoder.getDistance();
    }

    @Override
    public void resetTurretPosition() {
        rotationEncoder.reset();
    }

    @Override
    public boolean isAtLeftLimit() {
        return getTurretPosition() <= -100;
    }

    @Override
    public boolean isAtRightLimit() {
        return getTurretPosition() >= 100;
    }

    @Override
    public boolean isAtCenterLimit() {
        return Math.abs(getTurretPosition()) < 2.5;
    }

    @Override
    public double getBallDistance() {
        return ballDistance;
    }

    public void setBallDistance(double ballDistance) {
        this.ballDistance = ballDistance;
    }

    @Override
    public void configure(Config config) {
    }

    public static class Config {
    }
}
