package org.teamtators.rotator.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.TriggerAdder;

import java.util.Iterator;
import java.util.Map;

public class TriggerBinder {
    private Scheduler scheduler;
    private CommandStore commandStore;
    private ObjectMapper objectMapper;
    private AbstractOperatorInterface operatorInterface;
    private static final Logger logger = LoggerFactory.getLogger(TriggerBinder.class);

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

    @Inject
    public void setOperatorInterface(AbstractOperatorInterface operatorInterface) {
        this.operatorInterface = operatorInterface;
    }

    /**
     * Bind buttons to all joysticks using a TriggersConfig
     *
     * @param triggersConfig Triggers configuration object
     */
    public void bindButtonsToJoysticks(TriggersConfig triggersConfig) {
        bindButtonsToLogitechF310(triggersConfig.getDriver(), operatorInterface.driverJoystick());
    }

    /**
     * Bind buttons to all joysticks using a config node
     *
     * @param config Root config node of triggers
     */
    public void bindButtonsToJoysticks(JsonNode config) {
        TriggersConfig triggersConfig;
        try {
            triggersConfig = objectMapper.treeToValue(config, TriggersConfig.class);
        } catch (JsonProcessingException e) {
            throw new ConfigException("Failed to process triggers");
        }
        bindButtonsToJoysticks(triggersConfig);
    }

    /**
     * Bind buttons to a joystick using a TriggersConfig
     *
     * @param buttonsMap Map of buttons and their bindings
     * @param joystick   Joystick to bind buttons to
     */
    public void bindButtonsToLogitechF310(Map<LogitechF310.Button, String> buttonsMap, LogitechF310 joystick) {
        Iterator<Map.Entry<LogitechF310.Button, String>> it = buttonsMap.entrySet().iterator();
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
