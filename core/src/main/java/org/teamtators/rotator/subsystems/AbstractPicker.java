package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for picker
 * Picks up the ball with rollers
 */
public abstract class AbstractPicker extends Subsystem {
    private PickerPosition position = PickerPosition.HOME;

    public AbstractPicker() {
        super("Picker");
    }

    /**
     * Sets the power for the roller on the picker
     *
     * @param power Power for the motor
     */
    public abstract void setPower(double power);

    /**
     * Resets the power for the roller on the picker
     */
    public void resetPower() {
        setPower(0f);
    }

    /**
     * sets the picker's position
     *
     * @param position the picker's position
     */
    public void setPosition(PickerPosition position) {
        this.position = position;
    }

    /**
     * @return the picker's position
     */
    public PickerPosition getPosition() {
        return position;
    }
}
