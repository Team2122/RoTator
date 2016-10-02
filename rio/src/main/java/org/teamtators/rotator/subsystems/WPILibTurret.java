package org.teamtators.rotator.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.components.DigitalSensor;
import org.teamtators.rotator.components.DistanceLaser;
import org.teamtators.rotator.config.*;
import org.teamtators.rotator.control.ControllerTest;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.StateListener;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.components.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WPILibTurret extends AbstractTurret implements Configurable<WPILibTurret.Config>, ITestable, StateListener {

    @Inject
    ControllerFactory controllerFactory;
    @Inject
    Scheduler scheduler;
    @Inject
    ConfigCommandStore commandStore;
    private VictorSP pinchRollerMotor;
    private VictorSP kingRollerMotor;
    private DistanceLaser ballSensor;
    private Solenoid hoodDeploySolenoid;
    private Solenoid shortSolenoid;
    private Solenoid longSolenoid;
    private VictorSP shooterWheelMotor;
    private Encoder shooterWheelEncoder;
    private VictorSP turretRotationMotor;
    private Encoder turretRotationEncoder;
    private DigitalSensor leftLimit;
    private DigitalSensor rightLimit;
    private DigitalSensor centerLimit;
    private double maxBallDistance;

    @Inject
    public WPILibTurret() {
    }

    @Override
    public void configure(Config config) {
        this.pinchRollerMotor = config.pinchRollerMotor.create();
        this.kingRollerMotor = config.kingRollerMotor.create();
        this.ballSensor = config.ballSensor.create();
        this.hoodDeploySolenoid = new Solenoid(config.hoodDeploySolenoid);
        this.shortSolenoid = new Solenoid(config.shortSolenoid);
        this.longSolenoid = new Solenoid(config.longSolenoid);
        this.shooterWheelMotor = config.shooterWheelMotor.create();
        this.shooterWheelEncoder = config.shooterWheelEncoder.create();
        this.turretRotationMotor = config.turretRotationMotor.create();
        this.turretRotationEncoder = config.turretRotationEncoder.create();
        this.leftLimit = config.leftLimit.create();
        this.rightLimit = config.rightLimit.create();
        this.centerLimit = config.centerLimit.create();
        this.maxBallDistance = config.maxBallDistance;

        super.configure(config);
    }

    @Override
    public void setWheelPower(double power) {
        if (getBallDistance() <= maxBallDistance) {
            shooterWheelMotor.set((double) power);
        } else {
            logger.warn("Ball is not within safe distance, not moving turret");
        }
    }

    @Override
    public double getWheelSpeed() {
        return shooterWheelEncoder.getRate();
    }

    @Override
    public double getWheelRotations() {
        return shooterWheelEncoder.getDistance();
    }

    @Override
    public void onEnterState(RobotState newState) {
        switch (newState) {
            case AUTONOMOUS:
            case TELEOP:
                enableShooterWheelController();
                if (isHomed()) {
                    enableAngleController();
                } else {
                    scheduler.startCommand(commandStore.getCommand("TurretHome"));
                }
                break;
            default:
                disableShooterWheelController();
                disableAngleController();
                break;
        }
    }

    @Override
    public boolean homeTurret() {
        if (super.homeTurret()) {
            enableAngleController();
            return true;
        }
        return false;
    }

    @Override
    public void setHoodPosition(HoodPosition hoodPosition) {
        super.setHoodPosition(hoodPosition);
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
    public void setRotationPower(double power) {
        turretRotationMotor.set(power);
    }

    @Override
    public double getAngle() {
        return turretRotationEncoder.getDistance();
    }

    @Override
    public void resetAngleEncoder() {
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

    @Override
    public double getBallDistance() {
        return (float) ballSensor.getDistance();
    }

    public ComponentTestGroup getTestGroup() {
        return new ComponentTestGroup("Turret",
                new VictorSPTest("pinchRollerMotor", pinchRollerMotor),
                new VictorSPTest("kingRollerMotor", kingRollerMotor),
                new DistanceLaserTest("ballSensor", ballSensor),
                new SolenoidTest("hoodDeploySolenoid", hoodDeploySolenoid),
                new SolenoidTest("shortSolenoid", shortSolenoid),
                new SolenoidTest("longSolenoid", longSolenoid),
                new VictorSPTest("shooterWheelMotor", shooterWheelMotor),
                new EncoderTest("shooterWheelEncoder", shooterWheelEncoder),
                new ControllerTest(getShooterWheelController(), 120),
                new VictorSPTest("turretRotationMotor", turretRotationMotor),
                new EncoderTest("turretRotationEncoder", turretRotationEncoder),
                new ControllerTest(getAngleController(), 110),
                new DigitalSensorTest("leftLimit", leftLimit),
                new DigitalSensorTest("rightLimit", rightLimit),
                new DigitalSensorTest("centerLimit", centerLimit),
                new TurretTest());
    }

    public static class Config extends AbstractTurret.Config {
        public VictorSPConfig pinchRollerMotor;
        public VictorSPConfig kingRollerMotor;
        public DistanceLaserConfig ballSensor;
        public int hoodDeploySolenoid;
        public int shortSolenoid;
        public int longSolenoid;
        public VictorSPConfig shooterWheelMotor;
        public EncoderConfig shooterWheelEncoder;
        public VictorSPConfig turretRotationMotor;
        public EncoderConfig turretRotationEncoder;
        public DigitalSensorConfig leftLimit;
        public DigitalSensorConfig rightLimit;
        public DigitalSensorConfig centerLimit;
        public double maxBallDistance;
    }
}
