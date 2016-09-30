package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;

public class CancelCommand extends CommandBase implements Configurable<WaitForCommand.Config> {
    private CommandStore commandStore;

    private Command command;

    public CancelCommand(CoreRobot robot) {
        super("CancelCommand");
        commandStore = robot.commandStore();
    }

    @Override
    protected boolean step() {
        if (command.isRunning()) {
            command.cancel();
        } else {
            logger.warn(command.getName() + " not running, can't cancel it");
        }
        return true;
    }

    @Override
    public void configure(WaitForCommand.Config config) {
        command = commandStore.getCommand(config.command);
    }

    public static class Config {
        public String command;
    }
}
