package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.ITimeProvider;

/**
 * A command which does nothing for a specified amount of time
 */
public class WaitCommand extends CommandBase implements Configurable<WaitCommand.Config> {
    private Config config;
    private ITimeProvider timeProvider;
    private double startTime;

    public WaitCommand(CoreRobot robot) {
        super("WaitCommand");
        timeProvider = robot.timeProvider();
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    @Override
    protected void initialize() {
        logger.info("Waiting for a period of {} seconds", config.period);
        startTime = timeProvider.getTimestamp();
    }

    @Override
    public boolean step() {
        return timeProvider.getTimestamp() - startTime >= config.period;
    }

    static class Config {
        public double period;
    }
}
