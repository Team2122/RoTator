package org.teamtators.rotator.operatorInterface;

import org.teamtators.rotator.scheduler.TriggerSource;

public interface LogitechF310 {
    double getAxisValue(Axis axisKind);

    boolean getButtonValue(Button button);

    TriggerSource getTriggerSource(Button button);

    /**
     * Enum containing the location of all the buttons on a Logitech F310 gamepad
     */
    enum Button {
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

        private int buttonNumber;

        private Button(int buttonNumber) {
            this.buttonNumber = buttonNumber;
        }

        /**
         * Converts button name to enum
         *
         * @param name Name of the button
         * @return returns the type of button
         */

        public static Button fromName(String name) {
            return Button.valueOf(name);
        }

        /**
         * Gets the button number
         *
         * @return the number of the button
         */
        public int getButtonNumber() {
            return buttonNumber;
        }
    }

    /**
     * Enum containing the axes on a Logitech F310 joystick
     */
    enum Axis {
        LEFT_STICK_X(0),
        LEFT_STICK_Y(1),
        LEFT_TRIGGER(2),
        RIGHT_TRIGGER(3),
        RIGHT_STICK_X(4),
        RIGHT_STICK_Y(5);

        private int axisNumber;

        private Axis(int axisNumber) {
            this.axisNumber = axisNumber;
        }

        public int getAxisNumber() {
            return axisNumber;
        }
    }
}
