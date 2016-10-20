package org.teamtators.rotator;

import dagger.Module;
import dagger.Provides;
import org.teamtators.rotator.components.Chooser;
import org.teamtators.rotator.components.WPILibChooser;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.control.WPILibTimeProvider;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.WPILibOperatorInterface;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.subsystems.*;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Module(includes = CoreModule.class)
public class RioModule {
    // Subsystem providers
    @Provides
    static AbstractDrive providesDrive(WPILibDrive drive) {
        return drive;
    }

    @Provides
    static AbstractPicker providesPicker(WPILibPicker picker) {
        return picker;
    }

    @Provides
    static AbstractTurret providesTurret(WPILibTurret turret) {
        return turret;
    }

    @Provides
    static AbstractVision providesVision(WPILibVision vision) {
        return vision;
    }

    @Provides
    static AbstractOperatorInterface providesOperatorInterface(WPILibOperatorInterface oi) {
        return oi;
    }

    @Provides
    static ITimeProvider providesTimeProvider(WPILibTimeProvider timeProvider) {
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
        return "/home/lvuser/config";
    }

    @Provides
    @Named("dataLogDir")
    static String providesDataLogDir() {
        return "/media/sda1/datalogs";
    }

    @Provides
    @Singleton
    static Map<String, Chooser<Command>> providesCommandChoosers() {
        HashMap<String, Chooser<Command>> choosers = new HashMap<>();
        choosers.put("Auto", new WPILibChooser<>("Auto"));
        choosers.put("Backup", new WPILibChooser<>("Backup"));
        return choosers;
    }
}
