package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;

public class WaitForCommand extends CommandBase implements Configurable<WaitForCommand.Config> {
    private Command command;

    private CommandStore commandStore;

    @Override
    protected void initialize() {
        if (!command.isRunning()) {
            logger.debug("{} is not running", command.getName());
        }
    }

    @Override
    protected boolean step() {
        return !command.isRunning();
    }

    @Override
    public void configure(Config config) {
        this.command = commandStore.getCommand(config.command);
    }

    public WaitForCommand(CoreRobot robot) {
        super("WaitForCommand");
        commandStore = robot.commandStore();
    }

    public static class Config {
        public String command;
    }
}
