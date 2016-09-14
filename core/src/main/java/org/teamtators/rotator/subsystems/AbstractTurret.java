package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for turret
 * Shoots the ball
 */
public abstract class AbstractTurret extends Subsystem {
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

    public abstract double getWheelSpeed();

    /**
     * Set the wheel speed setpoint
     *
     * @param rps New wheel speed setpoint
     */
    public void setTargetWheelSpeed(double rps) {
        shooterWheelController.setSetpoint(rps);
    }

    /**
     * @return The wheel speed setpoint
     */
    public double getTargetWheelSpeed() {
        return getShooterWheelController().getSetpoint();
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

    protected void setShooterWheelController(AbstractController shooterWheelController) {
        shooterWheelController.setName("shooterWheelController");
        shooterWheelController.setInputProvider(this::getWheelSpeed);
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
        angleController.setLimitPredicate((delta, controller) -> isAtLeftLimit() || isAtRightLimit());
        angleController.setMinSetpoint(-100);
        angleController.setMaxOutput(100);
        this.angleController = angleController;
    }

    public void setTargetAngle(double targetAngle) {
        getAngleController().setSetpoint(targetAngle);
    }

    public double getTargetAngle() {
        return getAngleController().getSetpoint();
    }

    public boolean isAngleOnTarget() {
        return getAngleController().isOnTarget();
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
}
