package org.teamtators.rotator.subsystems;

import com.google.inject.Singleton;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.components.DigitalSensor;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.DigitalSensorConfig;
import org.teamtators.rotator.config.EncoderConfig;
import org.teamtators.rotator.config.VictorSPConfig;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.components.DigitalSensorTest;
import org.teamtators.rotator.tester.components.EncoderTest;
import org.teamtators.rotator.tester.components.SolenoidTest;
import org.teamtators.rotator.tester.components.VictorSPTest;

@Singleton
public class WPILibTurret extends AbstractTurret implements Configurable<WPILibTurret.Config>, ITestable {

    public static class Config {
        public VictorSPConfig shooterWheelMotor;
        public EncoderConfig shooterWheelEncoder;
        public int shortSolenoid;
        public int longSolenoid;
        public int hoodDeploySolenoid;
        public VictorSPConfig kingRollerMotor;
        public VictorSPConfig pinchRollerMotor;
        public VictorSPConfig turretRotationMotor;
        public EncoderConfig turretRotationEncoder;
        public DigitalSensorConfig leftLimit;
        public DigitalSensorConfig rightLimit;
        public DigitalSensorConfig centerLimit;
    }

    private VictorSP shooterWheelMotor;
    private Encoder shooterWheelEncoder;
    private Solenoid shortSolenoid;
    private Solenoid longSolenoid;
    private Solenoid hoodDeploySolenoid;
    private HoodPosition hoodPosition;
    private VictorSP kingRollerMotor;
    private VictorSP pinchRollerMotor;
    private VictorSP turretRotationMotor;
    private Encoder turretRotationEncoder;
    private DigitalSensor leftLimit;
    private DigitalSensor rightLimit;
    private DigitalSensor centerLimit;

    @Override
    public void configure(Config config) {
        this.shooterWheelMotor = config.shooterWheelMotor.create();
        this.shooterWheelEncoder = config.shooterWheelEncoder.create();
        this.shortSolenoid = new Solenoid(config.shortSolenoid);
        this.longSolenoid = new Solenoid(config.longSolenoid);
        this.hoodDeploySolenoid = new Solenoid(config.hoodDeploySolenoid);
        this.pinchRollerMotor = config.pinchRollerMotor.create();
        this.kingRollerMotor = config.kingRollerMotor.create();
        this.turretRotationMotor = config.turretRotationMotor.create();
        this.turretRotationEncoder = config.turretRotationEncoder.create();
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
    public double getWheelSpeed() {
        return shooterWheelEncoder.getRate();
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
    public void setPinchRollerPower(double power) {
        pinchRollerMotor.set(power);
    }

    @Override
    public void setKingRollerPower(double power) {
        kingRollerMotor.set(power);
    }

    @Override
    public void setTurretRotation(double power) {
        turretRotationMotor.set(power);
    }

    @Override
    public double getTurretPosition() {
        return turretRotationEncoder.getDistance();
    }

    @Override
    public void resetTurretPosition() {
        turretRotationEncoder.reset();
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

    public ComponentTestGroup getTestGroup() {
        return new ComponentTestGroup("Turret",
                new VictorSPTest("shooterWheelMotor", shooterWheelMotor),
                new EncoderTest("shooterWheelEncoder", shooterWheelEncoder),
                new SolenoidTest("shortSolenoid", shortSolenoid),
                new SolenoidTest("longSolenoid", longSolenoid),
                new SolenoidTest("hoodDeploySolenoid", hoodDeploySolenoid),
                new VictorSPTest("kingRollerMotor", kingRollerMotor),
                new VictorSPTest("pinchRollerMotor", pinchRollerMotor),
                new VictorSPTest("turretRotationMotor", turretRotationMotor),
                new EncoderTest("turretRotationEncoder", turretRotationEncoder),
                new DigitalSensorTest("leftLimit", leftLimit),
                new DigitalSensorTest("rightLimit", rightLimit),
                new DigitalSensorTest("centerLimit", centerLimit));
    }
}
