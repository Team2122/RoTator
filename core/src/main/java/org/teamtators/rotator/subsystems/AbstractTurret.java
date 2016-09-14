package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for turret
 * Shoots the ball
 */
public abstract class AbstractTurret extends Subsystem {
    private AbstractController shooterWheelController = null;
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

    public void setWheelSpeed(double rps) {
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
        setWheelSpeed(0.0);
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
     * Rotates the turret
     *
     * @param power power for the turret's rotation
     */
    public abstract void setTurretRotation(double power);

    /**
     * Resets the turret's rotation
     */
    public void resetTurretRotation() {
        setTurretRotation(0);
    }

    /**
     * @return the turret's position
     */
    public abstract double getTurretPosition();

    /**
     * Resets the turret's position
     */
    public abstract void resetTurretPosition();

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
            resetTurretPosition();
            homed = true;
            logger.info("Turret at center limit, successfully homed");
            return true;
        }
        return false;
    }
}
