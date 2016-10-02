package org.teamtators.rotator.subsystems;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.*;
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


    public AbstractTurret() {
        super("Turret");
    }

    /**
     * Sets the power for the roller that shoots the ball
     *
     * @param power The power of the roller that shoots
     */
    protected abstract void setWheelPower(double power);

    /**
     * Resets the power for the roller that shoots
     */
    protected void resetPower() {
        setWheelPower(0);
    }

    public double getWheelSpeed() {
        return shooterWheelInputDifferentiator.getControllerInput();
    }

    public abstract double getWheelRotations();

    /**
     * @return The wheel speed setpoint
     */
    public double getTargetWheelSpeed() {
        return getShooterWheelController().getSetpoint();
    }

    /**
     * Set the wheel speed setpoint
     *
     * @param rps New wheel speed setpoint
     */
    public void setTargetWheelSpeed(double rps) {
        shooterWheelController.setSetpoint(rps);
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

    /**
     * Sets the pinch roller's power
     *
     * @param power the power of the pinch roller
     */
    public abstract void setPinchRollerPower(double power);

    /**
     * Resets the pinch roller's power
     */
    public void resetPinchRollerPower() {
        setPinchRollerPower(0);
    }

    /**
     * Sets the king roller's power
     *
     * @param power the king roller's power
     */
    public abstract void setKingRollerPower(double power);

    /**
     * Resets the king roller's power
     */
    public void resetKingRollerPower() {
        setKingRollerPower(0);
    }

    /**
     * Sets the power to the turret rotation motor
     *
     * @param power power for the turret's rotation motor from -1 to 1
     */
    public abstract void setRotationPower(double power);

    /**
     * Resets the turret's rotation motor power
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
    }

    protected void configure(Config config) {
        setShooterWheelController(controllerFactory.create(config.shooterWheelController));
        setAngleController(controllerFactory.create(config.angleController));
    }

}
