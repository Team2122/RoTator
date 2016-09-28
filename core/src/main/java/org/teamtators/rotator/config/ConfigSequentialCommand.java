package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.SequentialCommand;

import java.util.ArrayList;
import java.util.Iterator;

public class ConfigSequentialCommand extends SequentialCommand implements Configurable<JsonNode> {
    private ConfigCommandStore commandStore;

    public ConfigSequentialCommand(ConfigCommandStore commandStore) {
        this("ConfigCommandStore", commandStore);
    }

    public ConfigSequentialCommand(String name, ConfigCommandStore commandStore) {
        super(name);
        this.commandStore = commandStore;
    }

    private String findNextCommandName(String className) {
        int postfix = 1;
        String name;
        do {
            name = String.format("%s<%s>%d", className, getName(), postfix);
            postfix++;
        } while (commandStore.getCommands().containsKey(name));
        return name;
    }

    @Override
    public void configure(JsonNode config) {
        if (config.size() != 0 && !config.isArray())
            throw new ConfigException("SequentialCommand config must be an array");
        Iterator<JsonNode> it = config.elements();
        ArrayList<SequentialCommandRun> sequence = new ArrayList<>();
        while (it.hasNext()) {
            JsonNode node = it.next();
            Command command;
            if (node.isObject()) {
                ObjectNode commandConfig = (ObjectNode) node;
                if (node.has("class")) {
                    String className = commandConfig.get("class").asText();
                    String commandName = findNextCommandName(className);
                    command = commandStore.constructCommandClass(commandName, className);
                    commandStore.configureCommand(command, commandConfig);
                } else if (node.has("name")) {
                    command = commandStore.getCommand(commandConfig.get("name").asText());
                } else {
                    throw new ConfigException("SequentialCommand config was passed object, but didn't contain class or command name");
                }
                SequentialCommandRun commandRun = new SequentialCommandRun(command);
                if(commandConfig.has("parallel")) {
                    if(commandConfig.get("parallel").asBoolean()) {
                        commandRun.parallel = true;
                    }
                }
                sequence.add(commandRun);
            } else if (node.isTextual()) {
                String commandName = node.asText();
                command = commandStore.getCommand(commandName);
                sequence.add(new SequentialCommandRun(command));
            } else {
                throw new ConfigException("Each node in a SequentialCommand config must be an object or a string," +
                        " not: " + node);
            }
        }
        setRunSequence(sequence);
    }
}
