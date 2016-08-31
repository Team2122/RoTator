package org.teamtators.rotator.tester.components;

import edu.wpi.first.wpilibj.VictorSP;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.tester.ComponentTest;

public class VictorSPTest extends ComponentTest {

    private VictorSP motor;
    private int fullspeed;
    private double axisValue;

    public VictorSPTest(String name, VictorSP motor) {
        super(name);
        this.motor = motor;
    }

    @Override
    public void start() {
        logger.info("Push joystick in direction to move, where forward is positive");
        logger.info("Press back/start to drive backward/forward at full speed");
        fullspeed = 0;
        axisValue = 0;
    }

    private double getSpeed() {
        if (fullspeed != 0) {
            return fullspeed;
        } else {
            return axisValue;
        }
    }

    @Override
    public void step() {
        motor.set(getSpeed());
    }

    @Override
    public void stop() {
        motor.set(0);
    }

    @Override
    public void onButtonDown(LogitechF310.Button button) {
        if (button == LogitechF310.Button.BACK) fullspeed--;
        else if (button == LogitechF310.Button.START) fullspeed++;
    }

    @Override
    public void onButtonUp(LogitechF310.Button button) {
        if (button == LogitechF310.Button.BACK) fullspeed++;
        else if (button == LogitechF310.Button.START) fullspeed--;
    }

    @Override
    public void updateAxis(double value) {
        axisValue = value;
    }
}
