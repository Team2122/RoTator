package org.teamtators.rotator;

import edu.wpi.first.wpilibj.Joystick;

/**
 * A class for interfacing with Logitech F310 joysticks
 */
public class LogitechF310 extends Joystick {

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
    public double getAxisValue(AxisKind axisKind) {
        return getRawAxis(axisKind.getAxisNumber());
    }

    /**
     * Get the axis value from the left trigger
     *
     * @return Axis value from the left trigger
     */
    public double getLeftTriggerAxis() {
        return getRawAxis(2);
    }

    /**
     * Get the axis value from the right trigger
     *
     * @return Axis value from the right trigger
     */
    public double getRightTriggerAxis() {
        return getRawAxis(3);
    }

    /**
     * Get the state of a given button
     *
     * @return State of the given button
     */
    public boolean getButtonValue(ButtonKind button) {
        if (button.equals(ButtonKind.TRIGGER_LEFT)) {
            return getLeftTriggerAxis() >= 0.5;
        } else if (button.equals(ButtonKind.TRIGGER_RIGHT)) {
            return getRightTriggerAxis() >= 0.5;
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
    public Button getButton(ButtonKind button) {
        return new Button(this, button);
    }

    /**
     * Get the currently pressed button
     *
     * @return ButtonKind of the currently pressed button
     */
    public ButtonKind getPressedButton() {
        for (ButtonKind i : ButtonKind.values()) {
            if (getButtonValue(i)) {
                return i;
            }
        }
        return ButtonKind.NONE;
    }

    /**
     * Enum containing the location of all the buttons on the gamepad
     */
    public enum ButtonKind {
        NONE(0),
        A(1),
        B(2),
        X(3),
        Y(4),
        BUMPER_LEFT(5),
        BUMPER_RIGHT(6),
        BACK(7),
        START(8),
        STICK_LEFT(9),
        STICK_RIGHT(10),
        TRIGGER_LEFT(11),
        TRIGGER_RIGHT(12),
        POV_UP(13),
        POV_DOWN(14),
        POV_LEFT(15),
        POV_RIGHT(16);

        /**
         * Gets the button number
         *
         * @return the number of the button
         */
        public int getButtonNumber() {
            return buttonNumber;
        }

        /**
         * Converts button name to enum
         *
         * @param name Name of the button
         * @return returns the type of button
         */

        public static ButtonKind fromName(String name) {
            return ButtonKind.valueOf(name);
        }

        private int buttonNumber;

        private ButtonKind(int buttonNumber) {
            this.buttonNumber = buttonNumber;
        }
    }

    /**
     * Enum for naming the joystick and axis
     */
    public enum AxisKind {
        LEFT_STICK_X(0),
        LEFT_STICK_Y(1),
        LEFT_TRIGGER(2),
        RIGHT_Trigger(3),
        RIGHT_STICK_X(4),
        RIGHT_STICK_Y(5);

        public int getAxisNumber() {
            return axisNumber;
        }

        private int axisNumber;

        private AxisKind(int axisNumber) {
            this.axisNumber = axisNumber;
        }
    }

    /**
     * A class representing a button on a LogitechF310 gamepad, used for binding commands to buttons
     */
    public class Button extends edu.wpi.first.wpilibj.buttons.Button {

        private Joystick joystick;
        private ButtonKind button;

        public Button(LogitechF310 joystick, ButtonKind button) {
            this.joystick = joystick;
            this.button = button;
        }

        public boolean get() {
            return getButtonValue(button);
        }
    }
}