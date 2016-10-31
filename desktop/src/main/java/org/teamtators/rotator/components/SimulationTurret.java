package org.teamtators.rotator.components;

import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Steppable;
import org.teamtators.rotator.tester.ComponentTestGroup;

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
    private double ballDistance;
    private double angleRange;

    @Inject
    public SimulationTurret() {
        shooterWheelEncoder.setMotor(shooterWheelMotor);
        rotationEncoder.setMotor(rotationMotor);
        kingRollerEncoder.setMotor(kingRollerMotor);
    }

    @Override
    public int getExecutionOrder() {
        return 200;
    }

    @Override
    public void step(double delta) {
        shooterWheelMotor.step(delta);
        shooterWheelEncoder.step(delta);
        rotationMotor.step(delta);
        rotationEncoder.step(delta);
        kingRollerMotor.step(delta);
        kingRollerEncoder.step(delta);
    }

    @Override
    public void setWheelPower(double power) {
        shooterWheelMotor.setPower(power);
    }

    @Override
    public double getWheelRate() {
        return shooterWheelEncoder.getRate();
    }

    @Override
    public double getWheelRotations() {
        return shooterWheelEncoder.getDistance();
    }

    @Override
    public void resetWheelRotations() {
        shooterWheelEncoder.resetRotations();
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
        return getAngle() <= -angleRange;
    }

    @Override
    public boolean isAtRightLimit() {
        return getAngle() >= angleRange;
    }

    @Override
    public boolean isAtCenterLimit() {
        return Math.abs(getAngle()) < 2.5;
    }

    @Override
    public double getBallDistance() {
        return ballDistance;
    }

    @Override
    public double getBallCompression() {
        return 0;
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
        this.angleRange = config.angleRange;
    }

    public static class Config {
        public SimulationMotor.Config shooterWheelMotor;
        public SimulationEncoder.Config shooterWheelEncoder;
        public SimulationMotor.Config rotationMotor;
        public SimulationEncoder.Config rotationEncoder;
        public SimulationMotor.Config kingRollerMotor;
        public SimulationEncoder.Config kingRollerEncoder;
        public double angleRange;
    }
}
