package org.teamtators.rotator;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.teamtators.rotator.commands.CoreCommands;
import org.teamtators.rotator.config.ConfigCommandStore;

public class CoreModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides @Singleton
    ConfigCommandStore provideConfigCommandStore(Injector injector) {
        ConfigCommandStore commandStore = new ConfigCommandStore();
        commandStore.setInjector(injector);
        CoreCommands.register(commandStore);
        return commandStore;
    }
}
