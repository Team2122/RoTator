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

    @Override
    public double getAxisValue(Axis axisKind) {
        return getRawAxis(axisKind.getAxisNumber());
    }

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
                    currentPOV = null;
            }
            return button.equals(currentPOV);
        } else {
            return getRawButton(button.getButtonNumber());
        }
    }
}