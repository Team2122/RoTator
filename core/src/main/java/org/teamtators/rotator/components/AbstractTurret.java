package org.teamtators.rotator.components;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.InputDifferentiator;
import org.teamtators.rotator.control.LimitPredicates;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.tester.ComponentTest;
import org.teamtators.rotator.tester.ComponentTestGroup;
import org.teamtators.rotator.tester.ITestable;

import javax.inject.Inject;

/**
 * Interface for turret
 * Shoots the ball
 */
public abstract class AbstractTurret extends Component implements ITestable {
    private HoodPosition hoodPosition = HoodPosition.DOWN;

    public AbstractTurret() {
        super("Turret");
    }

    /**
     * Sets the speed for the roller that shoots the ball
     *
     * @param power The speed of the roller that shoots
     */
    public abstract void setWheelPower(double power);

    /**
     * Resets the speed for the roller that shoots
     */
    public void resetWheelPower() {
        setWheelPower(0);
    }

    public abstract double getWheelRate();

    public abstract double getWheelRotations();

    public abstract void resetWheelRotations();

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

    @Override
    public ComponentTestGroup getTestGroup() {
        return new ComponentTestGroup("Turret");
    }
}
