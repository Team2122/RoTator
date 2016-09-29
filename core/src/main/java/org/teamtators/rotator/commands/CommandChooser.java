package org.teamtators.rotator.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.components.Chooser;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;
import org.teamtators.rotator.scheduler.Scheduler;

public class CommandChooser extends CommandBase implements Configurable<CommandChooser.Config> {
    private Chooser<Command> chooser;
    private CommandStore commandStore;
    private Scheduler scheduler;

    public CommandChooser(CoreRobot robot) {
        super("CommandChooser");
        chooser = robot.autoChooser();
        commandStore = robot.commandStore();
        scheduler = robot.scheduler();
    }

    @Override
    protected boolean step() {
        return true;
    }

    @Override
    public void configure(Config config) {
        for (String command : config.commands) {
            chooser.registerOption(command, commandStore.getCommand(command));
        }
        if (config.defaul != null) {
            chooser.registerOption(config.defaul,
                    commandStore.getCommand(config.defaul));
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        scheduler.startCommand(chooser.getSelected());
    }

    static class Config {
        public String[] commands;
        @JsonProperty("default")
        public String defaul;
    }
}
