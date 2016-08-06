package org.teamtators.rotator.operatorInterface;

import edu.wpi.first.wpilibj.Joystick;
import org.teamtators.rotator.scheduler.TriggerSource;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for interfacing with Logitech F310 joysticks
 */
public class WPILibLogitechF310 extends Joystick implements LogitechF310 {
    private Map<Button, TriggerSource> triggerSourceCache = new HashMap<>();

    /**
     * Constructor for a Logitech F310 joystick
     *
     * @param port Number of the port of the joystick is connected
     */
    public WPILibLogitechF310(int port) {
        super(port);
    }

    /**
     * A method to return the value of the joystick
     *
     * @param axisKind The stick and direction
     * @return the value of the axis
     */
    @Override
    public double getAxisValue(Axis axisKind) {
        return getRawAxis(axisKind.getAxisNumber());
    }

    /**
     * Get the state of a given button
     *
     * @return State of the given button
     */
    @Override
    public boolean getButtonValue(Button button) {
        if (button.equals(Button.TRIGGER_LEFT)) {
            return getAxisValue(Axis.LEFT_TRIGGER) >= 0.5;
        } else if (button.equals(Button.TRIGGER_RIGHT)) {
            return getAxisValue(Axis.RIGHT_TRIGGER) >= 0.5;
        } else if (button.compareTo(Button.POV_UP) >= 0 && button.compareTo(Button.POV_RIGHT) <= 0) {
            Button currentPOV;
            switch (getPOV()) {
                case 0:
                    currentPOV = Button.POV_UP;
                    break;
                case 90:
                    currentPOV = Button.POV_RIGHT;
                    break;
                case 180:
                    currentPOV = Button.POV_DOWN;
                    break;
                case 270:
                    currentPOV = Button.POV_LEFT;
                    break;
                default:
                    currentPOV = Button.NONE;
            }
            return button.equals(currentPOV);
        } else {
            return getRawButton(button.getButtonNumber());
        }
    }

    /**
     * Get the button for the given Button
     *
     * @param button Button to get the button for
     * @return A button for the given Button
     */
    @Override
    public TriggerSource getTriggerSource(Button button) {
        TriggerSource triggerSource = triggerSourceCache.get(button);
        if (triggerSource == null) {
            triggerSource = new LogitechTrigger(button);
            triggerSourceCache.put(button, triggerSource);
        }
        return triggerSource;
    }

    /**
     * A class representing a button on a WPILibLogitechF310 gamepad, used for binding commands to buttons
     */
    private class LogitechTrigger implements TriggerSource {
        private Button button;

        public LogitechTrigger(Button button) {
            this.button = button;
        }

        @Override
        public boolean getActive() {
            return WPILibLogitechF310.this.getButtonValue(button);
        }
    }
}