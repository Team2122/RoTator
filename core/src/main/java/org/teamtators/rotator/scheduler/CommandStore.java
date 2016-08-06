package org.teamtators.rotator.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CommandStore {
    protected final Logger logger = LogManager.getLogger();
    private Map<String, Command> commands = new HashMap<>();

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void putCommand(String name, Command command) {
        command.setName(name);
        commands.put(name, command);
    }

    public Command getCommand(String name) {
        Command command = commands.get(name);
        if (command == null)
            throw new IllegalArgumentException("No command with name \"" + name + "\" created");
        return command;
    }
}
