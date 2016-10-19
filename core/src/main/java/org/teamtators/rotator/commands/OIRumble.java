package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Timer;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.operatorInterface.RumbleType;

public class OIRumble extends CommandBase implements Configurable<OIRumble.Config> {

    private Config config;
    private int times;
    private Timer timer;
    private boolean rumbling;
    private LogitechF310 joystick;
    private AbstractOperatorInterface operatorInterface;

    public OIRumble(CoreRobot robot) {
        super("OIRumble");
        timer = new Timer(robot.timeProvider());
        this.operatorInterface = robot.operatorInterface();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
        if (config.joystick.equals("driver")) {
            joystick = operatorInterface.driverJoystick();
        } else if (config.joystick.equals("gunner")) {
            joystick = operatorInterface.gunnerJoystick();
        }
    }

    @Override
    protected void initialize() {
        logger.info("Rumbling {} {} times", config.type, config.maxTimes);
        times = 0;
        joystick.setRumble(config.type, (float) config.value);
        rumbling = true;
        timer.start();
    }

    @Override
    protected boolean step() {
        if (rumbling && timer.hasPeriodElapsed(config.onTime)) {
            joystick.setRumble(config.type, 0);
            rumbling = false;
            times++;
        } else if (!rumbling && timer.hasPeriodElapsed(config.offTime)) {
            joystick.setRumble(config.type, (float) config.value);
            rumbling = true;
        }
        return times >= config.maxTimes;
    }

    protected void finish() {
        joystick.setRumble(config.type, 0);
    }

    static class Config {
        public int maxTimes;
        public double onTime;
        public double offTime;
        public String joystick = "driver";
        public RumbleType type;
        public double value;
    }
}
