package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
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

    @Override
    public void configure(JsonNode config) {
        if (config.size() != 0 && !config.isArray())
            throw new ConfigException("SequentialCommand config must be an array");
        Iterator<JsonNode> it = config.elements();
        ArrayList<Command> sequence = new ArrayList<>();
        while (it.hasNext()) {
            JsonNode node = it.next();
            Command command = commandStore.getCommandForSubcontext(getName(), node);
            sequence.add(command);
        }
        setSequence(sequence);
    }
}
