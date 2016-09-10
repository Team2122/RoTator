package org.teamtators.rotator.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.TriggerAdder;

import java.util.Iterator;
import java.util.Map;

public class TriggerBinder {
    Scheduler scheduler;
    CommandStore commandStore;
    ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(TriggerBinder.class);

    @Inject
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Inject
    public void setCommandStore(CommandStore commandStore) {
        this.commandStore = commandStore;
    }

    @Inject
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Bind buttons to a joystick using a triggers config node
     *
     * @param config   Root config node of triggers
     * @param joystick Jostick to bind buttons to
     */
    public void bindButtonsToLogitechF310(JsonNode config, LogitechF310 joystick) {
        TriggersConfig triggersConfig;
        try {
            triggersConfig = objectMapper.treeToValue(config, TriggersConfig.class);
        } catch (JsonProcessingException e) {
            throw new ConfigException("Failed to process triggers");
        }
        bindButtonsToLogitechF310(triggersConfig, joystick);
    }

    /**
     * Bind buttons to a joystick using a TriggersConfig
     *
     * @param triggersConfig Triggers configuration
     * @param joystick       Joystick to bind buttons to
     */
    public void bindButtonsToLogitechF310(TriggersConfig triggersConfig, LogitechF310 joystick) {
        Iterator<Map.Entry<LogitechF310.Button, String>> it = triggersConfig.getDriver().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry line = it.next();
            TriggerAdder onTrigger = scheduler.onTrigger(joystick.getTriggerSource((LogitechF310.Button) line.getKey()));
            String bindingSpecifier = (String) line.getValue();
            String[] binding = bindingSpecifier.split(" ");
            Command command;
            try {
                command = commandStore.getCommand(binding[1]);
            } catch (IllegalArgumentException e) {
                throw new ConfigException("Command with name \"" + binding[1] + "\" in config does not exist");
            }
            switch (binding[0]) {
                case "WhenPressed":
                    onTrigger.start(command).whenPressed();
                    break;
                case "WhenReleased":
                    onTrigger.start(command).whenReleased();
                    break;
                case "ToggleWhenPressed":
                    onTrigger.toggle(command).whenPressed();
                    break;
                case "ToggleWhenReleased":
                    onTrigger.toggle(command).whenReleased();
                    break;
                case "WhilePressed":
                    onTrigger.whilePressed(command);
                    break;
                case "WhileReleased":
                    onTrigger.whileReleased(command);
                    break;
                default:
                    throw new ConfigException("Specified binding type " + binding[0] + " is invalid.");
            }
        }
    }
}
