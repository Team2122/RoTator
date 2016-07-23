package org.teamtators.rotator;

import edu.wpi.first.wpilibj.Joystick;
import org.teamtators.rotator.subsystems.scheduler.Trigger;

/**
 * A class for interfacing with Logitech F310 joysticks
 */
public class LogitechF310 extends Joystick implements ILogitechF310 {

    /**
     * Constructor for a Logitech F310 joystick
     *
     * @param port Number of the port of the joystick is connected
     */
    public LogitechF310(int port) {
        super(port);
    }

    /**
     * A method to return the value of the joystick
     *
     * @param axisKind The stick and direction
     * @return the value of the axis
     */
    @Override
    public double getAxisValue(AxisKind axisKind) {
        return getRawAxis(axisKind.getAxisNumber());
    }

    /**
     * Get the state of a given button
     *
     * @return State of the given button
     */
    @Override
    public boolean getButtonValue(ButtonKind button) {
        if (button.equals(ButtonKind.TRIGGER_LEFT)) {
            return getAxisValue(AxisKind.LEFT_TRIGGER) >= 0.5;
        } else if (button.equals(ButtonKind.TRIGGER_RIGHT)) {
            return getAxisValue(AxisKind.RIGHT_TRIGGER) >= 0.5;
        } else if (button.compareTo(ButtonKind.POV_UP) >= 0 && button.compareTo(ButtonKind.POV_RIGHT) <= 0) {
            ButtonKind currentPOV;
            switch (getPOV()) {
                case 0:
                    currentPOV = ButtonKind.POV_UP;
                    break;
                case 90:
                    currentPOV = ButtonKind.POV_RIGHT;
                    break;
                case 180:
                    currentPOV = ButtonKind.POV_DOWN;
                    break;
                case 270:
                    currentPOV = ButtonKind.POV_LEFT;
                    break;
                default:
                    currentPOV = ButtonKind.NONE;
            }
            return button.equals(currentPOV);
        } else {
            return getRawButton(button.getButtonNumber());
        }
    }

    /**
     * Get the button for the given ButtonKind
     *
     * @param button ButtonKind to get the button for
     * @return A button for the given ButtonKind
     */
    @Override
    public Trigger getTrigger(ButtonKind button) {
        return new LogitechTrigger(this, button);
    }

    /**
     * Get the currently pressed button
     *
     * @return ButtonKind of the currently pressed button
     */
    @Override
    public ButtonKind getPressedButton() {
        for (ButtonKind i : ButtonKind.values()) {
            if (getButtonValue(i)) {
                return i;
            }
        }
        return ButtonKind.NONE;
    }

    /**
     * A class representing a button on a LogitechF310 gamepad, used for binding commands to buttons
     */
    public static class LogitechTrigger implements Trigger {
        private LogitechF310 joystick;
        private ButtonKind button;

        public LogitechTrigger(LogitechF310 joystick, ButtonKind button) {
            this.joystick = joystick;
            this.button = button;
        }

        @Override
        public boolean getActive() {
            return joystick.getButtonValue(button);
        }
    }
}