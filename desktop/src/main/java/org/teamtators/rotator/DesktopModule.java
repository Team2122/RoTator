package org.teamtators.rotator;

import dagger.Module;
import dagger.Provides;
import org.teamtators.rotator.components.Chooser;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.control.SystemNanoTimeTimeProvider;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.operatorInterface.SimulationOperatorInterface;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.subsystems.*;
import org.teamtators.rotator.subsystems.noop.NoopVision;
import org.teamtators.rotator.ui.SimulationChooser;
import org.teamtators.rotator.ui.SimulationControl;
import org.teamtators.rotator.ui.WASDJoystick;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Module(includes = CoreModule.class)
public class DesktopModule {
    // Subsystem providers
    @Provides
    static AbstractDrive providesDrive(SimulationDrive drive) {
        return drive;
    }

    @Provides
    static AbstractPicker providesPicker(SimulationPicker picker) {
        return picker;
    }

    @Provides
    static AbstractTurret providesTurret(SimulationTurret turret) {
        return turret;
    }

    @Provides
    @Singleton
    static AbstractVision providesVision(/*SimulationVision vision*/) {
//        return vision;
        return new SimulationVision();
    }

    @Provides
    static LogitechF310 providesLogitechF310(WASDJoystick joystick) {
        return joystick;
    }

    @Provides
    static ITimeProvider providesTimeProvider(SystemNanoTimeTimeProvider timeProvider) {
        return timeProvider;
    }

    @Provides
    @Singleton
    static ScheduledExecutorService providesExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Provides
    @Named("configDir")
    static String providesConfigDir() {
        return "./config";
    }

    @Provides
    @Named("dataLogDir")
    static String providesDataLogDir() {
        return "./datalogs";
    }

    @Provides
    @Singleton
    static AbstractOperatorInterface providesOperatorInterface(LogitechF310 driverJoystick, LogitechF310 gunnerJoystick) {
        return new SimulationOperatorInterface(driverJoystick, gunnerJoystick);
    }

    @Provides
    @Named("autoChooser")
    static Chooser<Command> providesCommandChooser(SimulationControl control) {
        return new SimulationChooser<>(control);
    }
}
