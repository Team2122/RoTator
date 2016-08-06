package org.teamtators.rotator;

import org.teamtators.rotator.scheduler.Trigger;

public interface ILogitechF310 {
    double getAxisValue(AxisKind axisKind);

    boolean getButtonValue(ButtonKind button);

    Trigger getTrigger(ButtonKind button);

    ButtonKind getPressedButton();

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
        RIGHT_TRIGGER(3),
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
}
