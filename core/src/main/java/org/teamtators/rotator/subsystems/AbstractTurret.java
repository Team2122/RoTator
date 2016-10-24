package org.teamtators.rotator.subsystems;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.InputDifferentiator;
import org.teamtators.rotator.control.LimitPredicates;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.tester.ComponentTest;

import javax.inject.Inject;

/**
 * Interface for turret
 * Shoots the ball
 */
public abstract class AbstractTurret extends Subsystem {
    @Inject
    ControllerFactory controllerFactory;

    @Inject
    InputDifferentiator shooterWheelInputDifferentiator;

    private AbstractController shooterWheelController = null;
    private AbstractController angleController = null;
    private boolean homed = false;
    private HoodPosition hoodPosition = HoodPosition.DOWN;
    private BallAge ballAge = BallAge.NEW;
    private double wheelSpeedOffset = 0.0;
    private boolean hasShot = false;


    public AbstractTurret() {
        super("Turret");
    }

    /**
     * Sets the speed for the roller that shoots the ball
     *
     * @param power The speed of the roller that shoots
     */
    protected abstract void setWheelPower(double power);

    /**
     * Resets the speed for the roller that shoots
     */
    protected void resetPower() {
        setWheelPower(0);
    }

    public double getWheelSpeed() {
        return shooterWheelInputDifferentiator.getControllerInput();
    }

    public abstract double getWheelRotations();

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
        shooterWheelInputDifferentiator.setInputProvider(this::getWheelRotations);
        shooterWheelController.setInputProvider(shooterWheelInputDifferentiator);
        shooterWheelController.setOutputConsumer(this::setWheelPower);
        this.shooterWheelController = shooterWheelController;
    }

    public void resetWheelSpeed() {
        setTargetWheelSpeed(0.0);
    }

    /**
     * @return the hood's position
     */
    public HoodPosition getHoodPosition() {
        return hoodPosition;
    }

    /**
     * Sets the hood position
     *
     * @param hoodPosition the hood's position
     */
    public void setHoodPosition(HoodPosition hoodPosition) {
        this.hoodPosition = hoodPosition;
    }

    public double getWheelSpeedOffset() {
        return wheelSpeedOffset;
    }

    public void setWheelSpeedOffset(double wheelSpeedOffset) {
        this.wheelSpeedOffset = wheelSpeedOffset;
    }

    /**
     * Sets the pinch roller's speed
     *
     * @param power the speed of the pinch roller
     */
    public abstract void setPinchRollerPower(double power);

    /**
     * Resets the pinch roller's speed
     */
    public void resetPinchRollerPower() {
        setPinchRollerPower(0);
    }

    /**
     * Sets the king roller's speed
     *
     * @param power the king roller's speed
     */
    public abstract void setKingRollerPower(double power);

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
    public abstract void setRotationPower(double power);

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
    public abstract double getAngle();

    /**
     * Resets the turret's position encoder. Sets the new 0 point for getAngle
     */
    public abstract void resetAngleEncoder();

    protected AbstractController getAngleController() {
        return angleController;
    }

    protected void setAngleController(AbstractController angleController) {
        angleController.setName("angleController");
        angleController.setInputProvider(this::getAngle);
        angleController.setOutputConsumer(this::setRotationPower);
        angleController.setLimitPredicate(LimitPredicates.doubleLimits(this::isAtLeftLimit, this::isAtRightLimit));
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
     * @return whether or not the turret is all the way to the left
     */
    public abstract boolean isAtLeftLimit();

    /**
     * @return whether or not the turret is all the way to the right
     */
    public abstract boolean isAtRightLimit();

    /**
     * @return whether or not the turret is in the center
     */
    public abstract boolean isAtCenterLimit();

    /**
     * @return how far the ball is from the ballSensor
     */
    public abstract double getBallDistance();

    /**
     * Gets the value from the ball compression sensor.
     * @return The value from the ball compression sensor, between 0-1. Measured
     * in rotations of the analog encoder.
     */
    public abstract double getBallCompression();

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
        if (isAtCenterLimit()) {
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

    protected void configure(Config config) {
        setShooterWheelController(controllerFactory.create(config.shooterWheelController));
        setAngleController(controllerFactory.create(config.angleController));
        ballAge = config.defaultBallAge;
    }

}
