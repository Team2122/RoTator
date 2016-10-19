package org.teamtators.rotator.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.operatorInterface.RumbleType;
import org.teamtators.rotator.scheduler.TriggerSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EnumMap;

@Singleton
public class WASDJoystick implements LogitechF310, KeyListener {
    private static final Logger logger = LoggerFactory.getLogger(WASDJoystick.class);
    public static final int MAX_SPEED = 1;
    private boolean up;
    private boolean left;
    private boolean down;
    private boolean right;
    private double leftTrigger;
    private double rightTrigger;

    private EnumMap<Button, Boolean> buttonValues = new EnumMap<Button, Boolean>(Button.class);
    private EnumMap<RumbleType, Float> rumbleValues = new EnumMap<>(RumbleType.class);

    @Inject
    public WASDJoystick() {
        reset();
    }

    @Override
    public void setRumble(RumbleType rumbleType, float value) {
        rumbleValues.put(rumbleType, value);
    }

    public float getRumble(RumbleType rumbleType) {
        if(rumbleValues.containsKey(rumbleType)) {
            return rumbleValues.get(rumbleType);
        }
        return 0;
    }

    public void reset() {
        up = false;
        left = false;
        down = false;
        right = false;
        leftTrigger = 0;
        rightTrigger = 0;
        buttonValues.clear();
    }

    @Override
    public double getAxisValue(Axis axisKind) {
        switch (axisKind) {
            case LEFT_STICK_Y:
                if (left)
                    if (up || down) return 0;
                    else return MAX_SPEED;
                else if (down) return MAX_SPEED;
                else if (right || up) return -MAX_SPEED;
                else return 0;
            case RIGHT_STICK_Y:
                if (right)
                    if (up || down) return 0;
                    else return MAX_SPEED;
                else if (down) return MAX_SPEED;
                else if (left || up) return -MAX_SPEED;
                else return 0;
            case LEFT_TRIGGER:
                return leftTrigger;
            case RIGHT_TRIGGER:
                return rightTrigger;
        }
        return 0;
    }

    @Override
    public boolean getButtonValue(Button button) {
        if (buttonValues.containsKey(button)) {
            return buttonValues.get(button);
        }
        return false;
    }

    @Override
    public TriggerSource getTriggerSource(Button button) {
        return () -> getButtonValue(button);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        setKeyValue(e.getKeyCode(), true);
        logger.trace("Key {} pressed", e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setKeyValue(e.getKeyCode(), false);
        logger.trace("Key {} released");
    }

    private void setKeyValue(int keyCode, boolean value) {
        switch (keyCode) {
            case KeyEvent.VK_UP:
                up = value;
                break;
            case KeyEvent.VK_LEFT:
                left = value;
                break;
            case KeyEvent.VK_DOWN:
                down = value;
                break;
            case KeyEvent.VK_RIGHT:
                right = value;
                break;
            case KeyEvent.VK_K:
                buttonValues.put(Button.X, value);
                break;
            case KeyEvent.VK_O:
                buttonValues.put(Button.Y, value);
                break;
            case KeyEvent.VK_L:
                buttonValues.put(Button.A, value);
                break;
            case KeyEvent.VK_P:
                buttonValues.put(Button.B, value);
                break;
            case KeyEvent.VK_1:
                buttonValues.put(Button.BUMPER_LEFT, value);
                break;
            case KeyEvent.VK_0:
                buttonValues.put(Button.BUMPER_RIGHT, value);
                break;
            case KeyEvent.VK_2:
                leftTrigger = value ? 1.0 : 0.0;
                buttonValues.put(Button.TRIGGER_LEFT, value);
                break;
            case KeyEvent.VK_9:
                rightTrigger = value ? 1.0 : 0.0;
                buttonValues.put(Button.TRIGGER_RIGHT, value);
                break;
            case KeyEvent.VK_3:
                buttonValues.put(Button.STICK_LEFT, value);
                break;
            case KeyEvent.VK_8:
                buttonValues.put(Button.STICK_RIGHT, value);
                break;
            case KeyEvent.VK_5:
                buttonValues.put(Button.BACK, value);
                break;
            case KeyEvent.VK_6:
                buttonValues.put(Button.START, value);
                break;
            case KeyEvent.VK_W:
                buttonValues.put(Button.POV_UP, value);
                break;
            case KeyEvent.VK_A:
                buttonValues.put(Button.POV_LEFT, value);
                break;
            case KeyEvent.VK_S:
                buttonValues.put(Button.POV_DOWN, value);
                break;
            case KeyEvent.VK_D:
                buttonValues.put(Button.POV_RIGHT, value);
                break;
        }
    }
}
