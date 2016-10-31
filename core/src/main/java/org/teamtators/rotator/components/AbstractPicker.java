package org.teamtators.rotator.components;

import org.teamtators.rotator.scheduler.Subsystem;

/**
 * Interface for picker
 * Picks up the ball with rollers
 */
public abstract class AbstractPicker extends Component {
    private PickerPosition position = PickerPosition.HOME;

    public AbstractPicker() {
        super("Picker");
    }

    /**
     * Sets the speed for the roller on the picker
     *
     * @param power Power for the motor
     */
    public abstract void setPickPower(double power);

    /**
     * Resets the speed for the roller on the picker
     */
    public void resetPickPower() {
        setPickPower(0f);
    }

    /**
     * Sets the pinch roller's speed
     *
     * @param power the speed of the pinch roller
     */
    public abstract void setPinchPower(double power);

    /**
     * Resets the pinch roller's speed
     */
    public void resetPinchPower() {
        setPinchPower(0);
    }

    /**
     * @return the picker's position
     */
    public PickerPosition getPosition() {
        return position;
    }

    /**
     * sets the picker's position
     *
     * @param position the picker's position
     */
    public void setPosition(PickerPosition position) {
        this.position = position;
    }

    public abstract boolean isAtCheval();
}
