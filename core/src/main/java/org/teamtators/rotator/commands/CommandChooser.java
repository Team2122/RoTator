package org.teamtators.rotator.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;
import org.teamtators.rotator.scheduler.Scheduler;

public class CommandChooser extends CommandBase implements Configurable<CommandChooser.Config> {

    private IChooser<Command> chooser;
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
        for(JsonNode node: config.commands) {
            if(node.isTextual()) {
                chooser.registerOption(node.asText(), commandStore.getCommand(node.asText()));
            }
            else {
                throw new ConfigException("Commands are only accepted by name");
            }
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        scheduler.startCommand(chooser.getSelected());
    }

    static class Config {
        public JsonNode commands;
    }
}
