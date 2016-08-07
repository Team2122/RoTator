package org.teamtators.rotator.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.ILogitechF310;
import org.teamtators.rotator.scheduler.Trigger;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WASDJoystick implements ILogitechF310, KeyListener {
    private static final Logger logger = LoggerFactory.getLogger(WASDJoystick.class);

    private boolean up = false;
    private boolean left = false;
    private boolean down = false;
    private boolean right = false;

    @Override
    public double getAxisValue(AxisKind axisKind) {
        switch (axisKind) {
            case LEFT_STICK_Y:
                if (left)
                    if (up || down) return 0;
                    else return -1;
                else if (down) return -1;
                else if (right || up) return 1;
                else return 0;
            case RIGHT_STICK_Y:
                if (right)
                    if (up || down) return 0;
                    else return -1;
                else if (down) return -1;
                else if (left || up) return 1;
                else return 0;
        }
        return 0;
    }

    @Override
    public boolean getButtonValue(ButtonKind button) {
        return false;
    }

    @Override
    public Trigger getTrigger(ButtonKind button) {
        return () -> getButtonValue(button);
    }

    @Override
    public ButtonKind getPressedButton() {
        return null;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                up = true;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                left = true;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                down = true;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                right = true;
                break;
            default:
                return;
        }
        logger.trace("Key {} pressed. w {} a {} s {} d {}", e.getKeyCode(), up, left, down, right);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                up = false;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                left = false;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                down = false;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                right = false;
                break;
            default:
                return;
        }
        logger.trace("Key {} released. w {} a {} s {} d {}", e.getKeyCode(), up, left, down, right);
    }
}
