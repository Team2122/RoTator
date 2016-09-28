package org.teamtators.rotator.control;

import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class ControllerTest extends ComponentTest {
    private AbstractController controller;
    private double maxSetpoint;

    public ControllerTest(AbstractController controller, double maxSetpoint) {
        super(controller.getName());
        this.controller = controller;
        this.maxSetpoint = maxSetpoint;
    }

    @Override
    public void start() {
        logger.info("Testing {} {} (with max setpoint = {})", controller.getClass().getSimpleName(), controller.getName(), maxSetpoint);
        logger.info("Press A to disable, B to enable, X to get information and joystick to set setpoint");
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        switch (button) {
            case A:
                controller.disable();
                logger.info("Disabled controller");
                break;
            case B:
                controller.enable();
                logger.info("Enabled controller");
                break;
            case X:
                logger.info("Input = {}, Setpoint = {}, Output = {}, On Target = {}", controller.getInput(),
                        controller.getSetpoint(), controller.getOutput(), controller.isOnTarget());
                break;
        }
    }

    @Override
    public void updateAxis(double value) {
        if (controller.isEnabled()) {
            controller.setSetpoint(value * maxSetpoint);
        }
    }

    @Override
    public void stop() {
        controller.disable();
    }
}
