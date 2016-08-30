package org.teamtators.rotator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.operatorInterface.WPILibLogitechF310;
import org.teamtators.rotator.subsystems.AbstractDrive;
import org.teamtators.rotator.subsystems.WPILibDrive;

public class RioModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CoreModule());
        bind(AbstractDrive.class).to(WPILibDrive.class);
        bind(WPILibDrive.class).in(Singleton.class);
        bind(LogitechF310.class).to(WPILibLogitechF310.class);
        bind(String.class).annotatedWith(Names.named("configDir")).toInstance("/home/lvuser/config");
    }

    @Provides @Singleton
    ObjectMapper providesObjectMapper() {
        return new YAMLMapper();
    }

    @Provides @Singleton
    WPILibLogitechF310 providesJoystick() {
        return new WPILibLogitechF310(0);
    }
}
