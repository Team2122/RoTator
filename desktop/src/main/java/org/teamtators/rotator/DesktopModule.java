package org.teamtators.rotator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.subsystems.AbstractDrive;
import org.teamtators.rotator.subsystems.SimulationDrive;
import org.teamtators.rotator.ui.WASDJoystick;

public class DesktopModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CoreModule());
        bind(AbstractDrive.class).to(SimulationDrive.class);
        bind(LogitechF310.class).to(WASDJoystick.class);
        bind(String.class).annotatedWith(Names.named("configDir")).toInstance("./config");
    }

    @Provides @Singleton
    ObjectMapper providesObjectMapper() {
        return new YAMLMapper();
    }
}
