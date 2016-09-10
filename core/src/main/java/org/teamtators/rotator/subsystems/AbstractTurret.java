package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for turret
 * Shoots the ball
 */
public abstract class AbstractTurret extends Subsystem {

    public AbstractTurret() {
        super("Turret");
    }

    /**
     * Sets the power for the roller that shoots the ball
     *
     * @param power The power of the roller that shoots
     */
    public abstract void setWheelPower(float power);

    /**
     * Resets the power for the roller that shoots
     */
    public void resetPower() {
        setWheelPower(0);
    }

    public abstract double getWheelSpeed();

    /**
     * Sets the hood position
     *
     * @param hoodPosition the hood's position
     */
    public abstract void setHoodPosition(HoodPosition hoodPosition);

    /**
     * @return the hood's position
     */
    public abstract HoodPosition getHoodPosition();

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
    public abstract float getBallDistance();
}
