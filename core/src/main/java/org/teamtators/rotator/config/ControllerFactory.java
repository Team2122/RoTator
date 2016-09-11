package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.control.PIDController;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class ControllerFactory {
    @Inject
    private Provider<PIDController> pidControllerProvider;
    @Inject
    private ObjectMapper objectMapper;

    public AbstractController create(JsonNode config) {
        checkNotNull(config, "Controller config can not be null");
        PIDController controller = pidControllerProvider.get();
        Configurables.configureObject(controller, config, objectMapper);
        return controller;
    }
}
