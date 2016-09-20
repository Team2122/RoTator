package org.teamtators.rotator.subsystems;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Steppable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
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

    @Inject
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
    public void setRotationPower(double power) {
        rotationMotor.setPower(power);
    }

    @Override
    public double getAngle() {
        return rotationEncoder.getDistance();
    }

    @Override
    public void resetAngleEncoder() {
        rotationEncoder.reset();
    }

    @Override
    public boolean isAtLeftLimit() {
        return getAngle() <= getShooterWheelController().getMinSetpoint();
    }

    @Override
    public boolean isAtRightLimit() {
        return getAngle() >= getShooterWheelController().getMaxSetpoint();
    }

    @Override
    public boolean isAtCenterLimit() {
        return Math.abs(getAngle()) < 2.5;
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
        shooterWheelMotor.configure(config.shooterWheelMotor);
        shooterWheelEncoder.configure(config.shooterWheelEncoder);
        rotationMotor.configure(config.rotationMotor);
        rotationEncoder.configure(config.rotationEncoder);
        kingRollerMotor.configure(config.kingRollerMotor);
        kingRollerEncoder.configure(config.kingRollerEncoder);
        pinchRollerMotor.configure(config.pinchRollerMotor);
        pinchRollerEncoder.configure(config.pinchRollerEncoder);

        super.configure(config);
    }

    public static class Config extends AbstractTurret.Config {
        public SimulationMotor.Config shooterWheelMotor;
        public SimulationEncoder.Config shooterWheelEncoder;
        public SimulationMotor.Config rotationMotor;
        public SimulationEncoder.Config rotationEncoder;
        public SimulationMotor.Config kingRollerMotor;
        public SimulationEncoder.Config kingRollerEncoder;
        public SimulationMotor.Config pinchRollerMotor;
        public SimulationEncoder.Config pinchRollerEncoder;
    }
}
