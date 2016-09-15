package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.PIDController;
import org.teamtators.rotator.control.StepController;

import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

public class ControllerFactory {
    @Inject
    ObjectMapper objectMapper;

    @Inject
    Provider<PIDController> providerPIDController;
    @Inject
    Provider<StepController> providerStepController;

    @Inject
    public ControllerFactory() {
    }

    public AbstractController create(JsonNode config) {
        checkNotNull(config, "Controller config can not be null");
        ObjectNode configCopy = config.deepCopy();
        if (!config.has("type") || !config.get("type").isTextual()) {
            throw new ConfigException("Controller config missing type");
        }
        AbstractController controller;
        String type = configCopy.remove("type").asText();
        switch (type) {
            case "PID":
                controller = providerPIDController.get();
                break;
            case "step":
                controller = providerStepController.get();
                break;
            default:
                throw new ConfigException("Invalid controller type \"" + type + "\" in config");
        }
        Configurables.configureObject(controller, configCopy, objectMapper);
        return controller;
    }
}
