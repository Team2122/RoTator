package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provider;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.PIDController;
import org.teamtators.rotator.control.StepController;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ControllerFactory {
    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private Provider<PIDController> providerPIDController;
    @Inject
    private Provider<StepController> providerStepController;

    public AbstractController create(JsonNode config) {
        checkNotNull(config, "Controller config can not be null");
        if (!config.has("type") || config.get("type") != null) {
            throw new ConfigException("Controller config missing type");
        }
        AbstractController controller;
        switch(config.get("type").asText()) {
            case "PID":
                controller = providerPIDController.get();
                break;
            case "step":
                controller = providerStepController.get();
                break;
            default:
                throw new ConfigException("Invalid controller type in config");
        }
        Configurables.configureObject(controller, config, objectMapper);
        return controller;
    }
}
