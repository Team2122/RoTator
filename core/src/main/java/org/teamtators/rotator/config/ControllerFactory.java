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

    private Map<String, Provider<? extends AbstractController>> providerMap = new HashMap<>();

    public ControllerFactory() {
        providerMap.put("PID", providerPIDController);
        providerMap.put("step", providerStepController);
    }

    public AbstractController create(JsonNode config) {
        checkNotNull(config, "Controller config can not be null");
        if(!config.has("type")) {
            throw new ConfigException("Controller type missing");
        }
        AbstractController controller = providerMap.get(config.get("type").asText()).get();
        if(controller instanceof Configurable) {
            Configurables.configureObject(controller, config, objectMapper);
        }
        return controller;
    }
}
