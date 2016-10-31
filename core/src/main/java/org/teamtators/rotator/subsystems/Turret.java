package org.teamtators.rotator.subsystems;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.components.AbstractTurret;
import org.teamtators.rotator.components.BallAge;
import org.teamtators.rotator.components.HoodPosition;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.ControllerTest;
import org.teamtators.rotator.control.InputDifferentiator;
import org.teamtators.rotator.control.LimitPredicates;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.tester.ComponentTest;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Interface for turret
 * Shoots the ball
 */
@Singleton
public class Turret extends Subsystem implements ITestable, Configurable<Turret.Config> {
    private AbstractTurret turretImpl;

    @Inject
    ControllerFactory controllerFactory;

    @Inject
    InputDifferentiator shooterWheelInputDifferentiator;

    private AbstractController shooterWheelController = null;
    private AbstractController angleController = null;
    private boolean homed = false;
<<<<<<< 73489766bd4c99d1f9fa559e942f9b8c0810f38f:core/src/main/java/org/teamtators/rotator/subsystems/AbstractTurret.java
    private HoodPosition hoodPosition = HoodPosition.DOWN;
    private BallAge ballAge = BallAge.NEW;
=======
    private BallAge ballAge;
>>>>>>> Improved subsystems a bit:core/src/main/java/org/teamtators/rotator/subsystems/Turret.java
    private double wheelSpeedOffset = 0.0;
    private boolean hasShot = false;

    @Inject
    public Turret(AbstractTurret turretImpl) {
        super("Turret");
        this.turretImpl = turretImpl;
    }

    public double getWheelSpeed() {
        return shooterWheelInputDifferentiator.getControllerInput();
    }

    /**
     * @return The wheel speed setpoint in rpm
     */
    public double getTargetWheelSpeed() {
        return (getShooterWheelController().getSetpoint() * 60.0) - wheelSpeedOffset;
    }

    /**
     * Set the wheel speed setpoint
     *
     * @param rpm New wheel speed setpoint in rpm
     */
    public void setTargetWheelSpeed(double rpm) {
        if (rpm != 0)
            rpm += wheelSpeedOffset;
        if (rpm < 0)
            rpm = 0;
        shooterWheelController.setSetpoint(rpm / 60.0);
    }

    /**
     * @return whether or not the turret is at the desired speed
     */
    public boolean isAtTargetWheelSpeed() {
        return getShooterWheelController().isOnTarget();
    }

    protected AbstractController getShooterWheelController() {
        return shooterWheelController;
    }

    protected void enableShooterWheelController() {
        shooterWheelInputDifferentiator.enable();
        shooterWheelController.enable();
    }

    protected void disableShooterWheelController() {
        shooterWheelInputDifferentiator.disable();
        shooterWheelController.disable();
    }

    protected InputDifferentiator getShooterWheelInputDifferentiator() {
        return shooterWheelInputDifferentiator;
    }

    protected void setShooterWheelController(AbstractController shooterWheelController) {
        shooterWheelController.setName("shooterWheelController");
        shooterWheelInputDifferentiator.setInputProvider(turretImpl::getWheelRotations);
        shooterWheelController.setInputProvider(shooterWheelInputDifferentiator);
        shooterWheelController.setOutputConsumer(turretImpl::setWheelPower);
        this.shooterWheelController = shooterWheelController;
    }

    public void resetWheelSpeed() {
        setTargetWheelSpeed(0.0);
    }

    /**
     * @return the hood's position
     */
    public HoodPosition getHoodPosition() {
        return turretImpl.getHoodPosition();
    }

    /**
     * Sets the hood position
     *
     * @param hoodPosition the hood's position
     */
    public void setHoodPosition(HoodPosition hoodPosition) {
        turretImpl.setHoodPosition(hoodPosition);
    }

    public double getWheelSpeedOffset() {
        return wheelSpeedOffset;
    }

    public void setWheelSpeedOffset(double wheelSpeedOffset) {
        this.wheelSpeedOffset = wheelSpeedOffset;
    }

    /**
     * Sets the king roller's speed
     *
     * @param power the king roller's speed
     */
    public void setKingRollerPower(double power) {
        turretImpl.setKingRollerPower(power);
    }

    /**
     * Resets the king roller's speed
     */
    public void resetKingRollerPower() {
        setKingRollerPower(0);
    }

    /**
     * Sets the speed to the turret rotation motor
     *
     * @param power speed for the turret's rotation motor from -1 to 1
     */
    public void setRotationPower(double power) {
        turretImpl.setRotationPower(power);
    }

    /**
     * Resets the turret's rotation motor speed
     */
    public void resetRotationPower() {
        setRotationPower(0);
    }

    /**
     * @return The rotation angle of the turret in degrees. 0 is wherever the turret
     * was when it was last reset
     */
    public double getAngle() {
        return turretImpl.getAngle();
    }

    /**
     * Resets the turret's position encoder. Sets the new 0 point for getAngle
     */
    public void resetAngleEncoder() {
        turretImpl.resetAngleEncoder();
    }

    protected AbstractController getAngleController() {
        return angleController;
    }

    protected void setAngleController(AbstractController angleController) {
        angleController.setName("angleController");
        angleController.setInputProvider(this::getAngle);
        angleController.setOutputConsumer(this::setRotationPower);
        angleController.setLimitPredicate(LimitPredicates.doubleLimits(turretImpl::isAtLeftLimit, turretImpl::isAtRightLimit));
        this.angleController = angleController;
    }

    public double getTargetAngle() {
        return getAngleController().getSetpoint();
    }

    public void setTargetAngle(double targetAngle) {
        getAngleController().setSetpoint(targetAngle);
    }

    public boolean isAngleOnTarget() {
        return getAngleController().isOnTarget();
    }

    protected void enableAngleController() {
        getAngleController().enable();
    }

    protected void disableAngleController() {
        getAngleController().disable();
    }

    /**
     * @return how far the ball is from the ballSensor
     */
    public double getBallDistance() {
        return turretImpl.getBallDistance();
    }

    /**
     * Gets the value from the ball compression sensor.
     * @return The value from the ball compression sensor, between 0-1. Measured
     * in rotations of the analog encoder.
     */
    public double getBallCompression() {
        return turretImpl.getBallCompression();
    }

    public void setBallAge(BallAge ballAge) {
        this.ballAge = ballAge;
    }

    public BallAge getBallAge() {
        return ballAge;
    }

    public boolean isHomed() {
        return homed;
    }

    protected void setHomed(boolean homed) {
        this.homed = homed;
    }

    public boolean homeTurret() {
        if (turretImpl.isAtCenterLimit()) {
            resetAngleEncoder();
            homed = true;
            logger.info("Turret at center limit, successfully homed");
            return true;
        }
        return false;
    }

    public void startShooting() {
        hasShot = false;
    }

    public void shot() {
        hasShot = true;
    }

    public boolean hasShot() {
        return hasShot;
    }

    public boolean isAtRightLimit() {
        return turretImpl.isAtRightLimit();
    }

    public boolean isAtLeftLimit() {
        return turretImpl.isAtLeftLimit();
    }

    @Override
    public ComponentTestGroup getTestGroup() {
        ComponentTestGroup group = turretImpl.getTestGroup();
        group.addTest(new ControllerTest(getShooterWheelController(), 120));
        group.addTest(new ControllerTest(getAngleController(), 110));
        group.addTest(new TurretTest());
        return group;
    }

    protected class TurretTest extends ComponentTest {
        public TurretTest() {
            super("TurretTest");
        }

        @Override
        public void start() {
            logger.info("Press A to attempt to home turret");
        }

        @Override
        public void onButtonDown(LogitechF310.Button button) {
            switch (button) {
                case A:
                    if (!homeTurret())
                        logger.info("Turret did not home");
                    break;
            }
        }
    }

    protected static class Config {
        public JsonNode shooterWheelController;
        public JsonNode angleController;
        public BallAge defaultBallAge;
    }

    @Override
    public void configure(Config config) {
        setShooterWheelController(controllerFactory.create(config.shooterWheelController));
        setAngleController(controllerFactory.create(config.angleController));
        ballAge = config.defaultBallAge;
    }
}
