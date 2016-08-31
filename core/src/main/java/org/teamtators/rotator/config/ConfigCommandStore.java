package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.Injector;
import org.teamtators.rotator.scheduler.Command;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.ProviderException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigCommandStore extends org.teamtators.rotator.scheduler.CommandStore {
    protected ObjectMapper objectMapper = new YAMLMapper();
    private Injector injector;
    private Map<String, Provider<Command>> commandProviders = new HashMap<>();
    private Map<String, JsonNode> defaultConfigs = new HashMap<>();

    @Inject
    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    public static ObjectNode applyDefaults(ObjectNode object, ObjectNode defaults) {
        ObjectNode result = defaults.deepCopy();
        Iterator<Map.Entry<String, JsonNode>> it = object.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            result.set(field.getKey(), field.getValue());
        }
        return result;
    }

    public void registerCommandProviders(Map<String, Provider<Command>> commandProviders) {
        this.commandProviders.putAll(commandProviders);
    }

    public Map<String, Provider<Command>> getCommandProviders() {
        return commandProviders;
    }

    public void registerCommand(String name, Provider<Command> constructor) {
        commandProviders.put(name, constructor);
    }

    public Provider<Command> getCommandProvider(String className) throws ConfigException {
        Provider<Command> provider = commandProviders.get(className);
        if (provider == null) {
            throw new ConfigException(String.format("Missing Command provider \"%s\"", className));
        }
        return provider;
    }

    public <T extends Command> void registerClass(Class<T> commandClass, String name) {
        Constructor<T> constructor;
        try {
            constructor = commandClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(commandClass.toString() + " does not have a no argument constructor");
        }
        Provider<Command> commandProvider = () -> {
            try {
                return constructor.newInstance();
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new ProviderException("Error constructing command", e);
            }
        };
        registerCommand(name, commandProvider);
    }

    public <T extends Command> void registerClass(Class<T> commandClass) {
        registerClass(commandClass, commandClass.getSimpleName());
    }

    public void createCommandsFromConfig(ObjectNode json) throws ConfigException {
        Iterator<Map.Entry<String, JsonNode>> it = json.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            String commandName = field.getKey();
            char prefix = commandName.charAt(0);
            if (prefix == '$') { // Sequential command config
                putCommand(commandName, new ConfigSequentialCommand(this));
            } else if (prefix == '^') {
                defaultConfigs.put(commandName.substring(1), field.getValue());
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
        if (config.isObject() && config.has("class")) {
            ObjectNode objectConfig = (ObjectNode) config;
            String className = objectConfig.remove("class").asText();
            JsonNode defaultConfig = defaultConfigs.get(className);
            if (defaultConfig != null && defaultConfig.isObject()) {
                config = applyDefaults(objectConfig, (ObjectNode) defaultConfig);
            }
        }
        Configurables.configureObject(command, config, objectMapper);
    }

    public Command createCommandFromConfig(String commandName, JsonNode config) throws ConfigException {
        String className;
        JsonNode classNode = config.get("class");
        if (classNode != null) {
            className = classNode.asText();
        } else {
            className = commandName;
        }
        return constructCommandClass(commandName, className);
    }

    public Command constructCommandClass(String commandName, String className) throws ConfigException {
        Provider<Command> constructor = getCommandProvider(className);
        Command command;
        try {
            command = constructor.get();
            if (injector != null)
                injector.injectMembers(command);
        } catch (Exception e) {
            throw new ConfigException("Exception thrown while constructing Command " + commandName, e);
        }
        putCommand(commandName, command);
        return command;
    }
}