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
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.scheduler.CommandStore;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.subsystems.AbstractDrive;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.AbstractVision;
import org.teamtators.rotator.tester.ManualTester;

import java.util.Arrays;
import java.util.List;

public class CoreModule extends AbstractModule {
    private String configDir = null;

    public CoreModule withConfigDir(String configDir) {
        this.configDir = configDir;
        return this;
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("configDir")).toInstance(configDir);
        bind(CommandStore.class).to(ConfigCommandStore.class);
    }

    @Provides
    @Singleton
    ObjectMapper providesObjectMapper() {
        return new YAMLMapper();
    }

    @Provides
    @Singleton
    ConfigCommandStore provideConfigCommandStore(Injector injector) {
        ConfigCommandStore commandStore = new ConfigCommandStore();
        commandStore.setInjector(injector);
        CoreCommands.register(commandStore);
        return commandStore;
    }

    @Provides
    @ForController
    @Singleton
    Stepper providesStepperForController() {
        return new Stepper(1.0 / 120.0);
    }

    @Provides
    @Singleton
    ManualTester providesManualTester(AbstractOperatorInterface operatorInterface, Scheduler scheduler) {
        ManualTester manualTester = new ManualTester();
        manualTester.setJoystick(operatorInterface.driverJoystick());
        scheduler.registerDefaultCommand(manualTester);
        return manualTester;
    }

    @Provides
    @Singleton
    public List<Subsystem> providesSubsystems(AbstractDrive drive, AbstractPicker picker, AbstractTurret turret,
                                              AbstractOperatorInterface operatorInterface, AbstractVision vision) {
        return Arrays.asList(drive, picker, turret, operatorInterface, vision);
    }
}
