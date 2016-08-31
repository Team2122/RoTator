package org.teamtators.rotator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.operatorInterface.WPILibLogitechF310;
import org.teamtators.rotator.operatorInterface.WPILibOperatorInterface;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.subsystems.AbstractDrive;
import org.teamtators.rotator.subsystems.WPILibDrive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RioModule extends AbstractModule {
    private ObjectNode commandsConfig;
    private ObjectNode subsystemsConfig;

    public RioModule withCommandsConfig(ObjectNode commandsConfig) {
        this.commandsConfig = commandsConfig;
        return this;
    }

    public RioModule withSubsystemsConfig(ObjectNode subsystemsConfig) {
        this.subsystemsConfig = subsystemsConfig;
        return this;
    }

    @Override
    protected void configure() {
        // Subsystem bindings
        bind(AbstractDrive.class).to(WPILibDrive.class);
        bind(AbstractOperatorInterface.class).to(WPILibOperatorInterface.class);

        if (commandsConfig != null)
            bind(ObjectNode.class).annotatedWith(Names.named("commands")).toInstance(commandsConfig);
        if (subsystemsConfig != null)
            bind(ObjectNode.class).annotatedWith(Names.named("subsystems")).toInstance(subsystemsConfig);
    }

    @Provides
    @Singleton
    public List<Subsystem> providesSubsystems(AbstractDrive drive, AbstractOperatorInterface operatorInterface) {
        return Arrays.asList(drive, operatorInterface);
    }
}
