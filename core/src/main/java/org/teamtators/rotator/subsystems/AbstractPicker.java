package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for picker
 * Picks up the ball with rollers
 */
public abstract class AbstractPicker extends Subsystem {
    public AbstractPicker() {
        super("Picker");
    }

    /**
     * Sets the power for the roller on the picker
     *
     * @param power Power for the motor
     */
    public abstract void setPower(float power);

    /**
     * Resets the power for the roller on the picker
     */
    public void resetPower() {
        setPower(0f);
    }

    /**
     * @return the power the motor is set to
     */
    public abstract double getPower();

    /**
     * sets the picker's position
     *
     * @param position the picker's position
     */
    public abstract void setPosition(PickerPosition position);

    /**
     * @return the picker's position
     */
    public abstract PickerPosition getPosition();
}
