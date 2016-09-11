package org.teamtators.rotator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.teamtators.rotator.commands.CoreCommands;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.control.ForController;
import org.teamtators.rotator.control.Stepper;

public class CoreModule extends AbstractModule {
    private String configDir = null;

    public CoreModule withConfigDir(String configDir) {
        this.configDir = configDir;
        return this;
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("configDir")).toInstance(configDir);
    }

    @Provides @Singleton
    ObjectMapper providesObjectMapper() {
        return new YAMLMapper();
    }

    @Provides @Singleton
    ConfigCommandStore provideConfigCommandStore(Injector injector) {
        ConfigCommandStore commandStore = new ConfigCommandStore();
        commandStore.setInjector(injector);
        CoreCommands.register(commandStore);
        return commandStore;
    }

    @Provides @ForController @Singleton
    Stepper providesStepperForController() {
        return new Stepper(1.0 / 120.0);
    }
}
