package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigCommandStore extends org.teamtators.rotator.scheduler.CommandStore {
    protected ObjectMapper objectMapper = new YAMLMapper();
    private Map<String, CommandConstructor> constructors = new HashMap<>();

    public static ObjectNode applyDefaults(ObjectNode object, ObjectNode defaults) {
        ObjectNode result = defaults.deepCopy();
        Iterator<Map.Entry<String, JsonNode>> it = object.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            result.set(field.getKey(), field.getValue());
        }
        return result;
    }

    public Map<String, CommandConstructor> getConstructors() {
        return constructors;
    }

    public void registerConstructor(String name, CommandConstructor constructor) {
        constructors.put(name, constructor);
    }

    public CommandConstructor getConstructor(String className) throws ConfigException {
        CommandConstructor constructor = constructors.get(className);
        if (constructor == null) {
            throw new ConfigException(String.format("Missing Command constructor \"%s\"", className));
        }
        return constructor;
    }

    public <T extends Command> void registerClass(Class<T> commandClass, String name) {
        Constructor<T> constructor;
        try {
            constructor = commandClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(commandClass.toString() + " does not have a no argument constructor");
        }
        CommandConstructor commandConstructor = () -> {
            try {
                return constructor.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                throw new InvocationTargetException(e);
            }
        };
        registerConstructor(name, commandConstructor);
    }

    public <T extends Command> void registerClass(Class<T> commandClass) {
        registerClass(commandClass, commandClass.getSimpleName());
    }

    public void createCommandsFromConfig(ObjectNode json) throws ConfigException {
        Iterator<Map.Entry<String, JsonNode>> it = json.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            String commandName = field.getKey();
            if (commandName.charAt(0) == '$') { // Sequential command config
                putCommand(commandName, new ConfigSequentialCommand(this));
            } else {
                createCommandFromConfig(commandName, field.getValue());
            }
        }
        for (Map.Entry<String, Command> commandEntry : getCommands().entrySet()) {
            Command command = commandEntry.getValue();
            JsonNode config = json.get(command.getName());
            configureCommand(command, config);
        }
    }

    public void configureCommand(Command command, JsonNode config) throws ConfigException {
        Configurables.configureObject(command, config, objectMapper);
    }

    public Command createCommandFromConfig(String commandName, JsonNode config) throws ConfigException {
        String className;
        if (config.has("class")) {
            className = ((ObjectNode) config).remove("class").asText();
        } else {
            className = commandName;
        }
        return constructCommandClass(commandName, className);
    }

    public Command constructCommandClass(String commandName, String className) throws ConfigException {
        CommandConstructor constructor = getConstructor(className);
        Command command;
        try {
            command = constructor.constructCommand();
        } catch (InvocationTargetException e) {
            throw new ConfigException("Exception thrown while constructing Command " + commandName, e);
        }
        putCommand(commandName, command);
        return command;
    }
}
