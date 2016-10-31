package org.teamtators.rotator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import dagger.Module;
import dagger.Provides;
import org.teamtators.rotator.commands.CoreCommands;
import org.teamtators.rotator.components.*;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.control.ForController;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.scheduler.CommandStore;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.subsystems.Drive;
import org.teamtators.rotator.subsystems.Turret;
import org.teamtators.rotator.tester.ManualTester;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Module
public class CoreModule {

    @Provides
    static CommandStore providesCommandStore(ConfigCommandStore configCommandStore) {
        return configCommandStore;
    }

    @Provides
    @Singleton
    static ObjectMapper providesObjectMapper() {
        return new YAMLMapper();
    }

    @Provides
    @Singleton
    ConfigCommandStore provideConfigCommandStore() {
        ConfigCommandStore commandStore = new ConfigCommandStore();
        CoreCommands.register(commandStore);
        return commandStore;
    }

    @Provides
    @ForController
    @Singleton
    Stepper providesStepperForController() {
        return new Stepper(1.0 / 60.0);
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
    @Named("subsystems")
    public List<Subsystem> providesSubsystems(Drive drive, Turret turret) {
        return Arrays.asList(drive, turret);
    }

    @Provides
    @Singleton
    @Named("components")
    public List<Component> providesComponents(AbstractDrive drive, AbstractPicker picker, AbstractTurret turret,
                                              AbstractOperatorInterface operatorInterface, AbstractVision vision) {
        return Arrays.asList(drive, picker, turret, operatorInterface, vision);
    }
}
