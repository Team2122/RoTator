package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Timer;
import org.teamtators.rotator.subsystems.AbstractVision;

public class VisionFlashyFlash extends CommandBase implements Configurable<VisionFlashyFlash.Config> {

    private Config config;
    private int times;
    private Timer timer;
    private AbstractVision vision;

    public VisionFlashyFlash(CoreRobot robot) {
        super("VisionFlashyFlash");
        vision = robot.vision();
        requires(vision);
        this.timer = robot.timer();
    }

    @Override
    public void configure(VisionFlashyFlash.Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        logger.info("Flashing light {} times", config.maxTimes);
        times = 0;
        vision.setLedState(true);
        timer.start();
    }

    @Override
    protected boolean step() {
        boolean blinding = vision.getLedState();
        if (blinding && timer.hasPeriodElapsed(config.onTime)) {
            vision.setLedState(false);
            times++;
        } else if (!blinding && timer.hasPeriodElapsed(config.offTime)) {
            vision.setLedState(true);
        }
        return times >= config.maxTimes;
    }

    protected void finish() {
        vision.setLedState(false);
    }

    static class Config {
        public int maxTimes;
        public float onTime;
        public float offTime;
    }
}
