package org.teamtators.rotator.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.components.DigitalSensor;
import org.teamtators.rotator.config.DigitalSensorConfig;
import org.teamtators.rotator.config.EncoderConfig;
import org.teamtators.rotator.config.VictorSPConfig;

public class WPILibTurret extends AbstractTurret implements Configurable<WPILibTurret.Config> {

    public static class Config {
        public VictorSPConfig shooterWheelMotor;
        public EncoderConfig encoder;
        public int shortSolenoid;
        public int longSolenoid;
        public int hoodDeploySolenoid;
        public double ballDist;
        public float power;
        public VictorSPConfig kingRollerMotor;
        public VictorSPConfig pinchRollerMotor;
        public VictorSPConfig turretRotationMotor;
        public DigitalSensorConfig leftLimit;
        public DigitalSensorConfig rightLimit;
        public DigitalSensorConfig centerLimit;
    }

    private VictorSP shooterWheelMotor;
    private Encoder encoder;
    private Solenoid shortSolenoid;
    private Solenoid longSolenoid;
    private Solenoid hoodDeploySolenoid;
    private HoodPosition hoodPosition;
    private double ballDist;
    private float power;
    private VictorSP kingRollerMotor;
    private VictorSP pinchRollerMotor;
    private VictorSP turretRotationMotor;
    private DigitalSensor leftLimit;
    private DigitalSensor rightLimit;
    private DigitalSensor centerLimit;

    @Override
    public void configure(Config config) {
        this.shooterWheelMotor = config.shooterWheelMotor.create();
        this.encoder = config.encoder.create();
        this.ballDist = config.ballDist;
        this.power = config.power;
        this.shortSolenoid = new Solenoid(config.shortSolenoid);
        this.longSolenoid = new Solenoid(config.longSolenoid);
        this.hoodDeploySolenoid = new Solenoid(config.hoodDeploySolenoid);
        this.pinchRollerMotor = config.pinchRollerMotor.create();
        this.kingRollerMotor = config.kingRollerMotor.create();
        this.turretRotationMotor = config.turretRotationMotor.create();
        this.leftLimit = config.leftLimit.create();
        this.rightLimit = config.rightLimit.create();
        this.centerLimit = config.centerLimit.create();
    }

    @Override
    public void setWheelPower(float power) {
        shooterWheelMotor.set((double) power);
    }

    @Override
    public void resetPower() {
        super.resetPower();
    }

    @Override
    public void setHoodPosition(HoodPosition hoodPosition) {
        switch (hoodPosition) {
            case DOWN:
                shortSolenoid.set(false);
                longSolenoid.set(false);
                hoodDeploySolenoid.set(false);
                break;
            case UP1:
                shortSolenoid.set(false);
                longSolenoid.set(false);
                hoodDeploySolenoid.set(true);
                break;
            case UP2:
                shortSolenoid.set(true);
                longSolenoid.set(false);
                hoodDeploySolenoid.set(true);
                break;
            case UP3:
                shortSolenoid.set(false);
                longSolenoid.set(true);
                hoodDeploySolenoid.set(true);
                break;
            case UP4:
                shortSolenoid.set(true);
                longSolenoid.set(true);
                hoodDeploySolenoid.set(true);
                break;
        }
        this.hoodPosition = hoodPosition;
    }

    @Override
    public HoodPosition getHoodPosition() {
        return hoodPosition;
    }

    @Override
    public void setPinchRollerPower(float power) {
        pinchRollerMotor.set((double) power);
    }

    @Override
    public void setKingRollerPower(float power) {
        kingRollerMotor.set((double) power);
    }

    @Override
    public void setTurretRotation(float power) {
        turretRotationMotor.set((double) power);
    }

    @Override
    public int getTurretPosition() {
        return encoder.get();
    }

    @Override
    public void resetTurretPosition() {
        encoder.reset();
    }

    @Override
    public boolean isAtRightLimit() {
        return rightLimit.get();
    }

    @Override
    public boolean isAtLeftLimit() {
        return leftLimit.get();
    }

    @Override
    public boolean isAtCenterLimit() {
        return centerLimit.get();
    }
}
