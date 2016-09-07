package org.teamtators.rotator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.control.WPILibTimeProvider;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.WPILibOperatorInterface;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.subsystems.*;

import java.util.Arrays;
import java.util.List;

public class RioModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CoreModule()
                .withConfigDir("/home/lvuser/config"));

        // Subsystem bindings
        bind(AbstractDrive.class).to(WPILibDrive.class);
        bind(AbstractPicker.class).to(WPILibPicker.class);
        bind(AbstractTurret.class).to(WPILibTurret.class);
        bind(AbstractVision.class).to(WPILibVision.class);
        bind(AbstractOperatorInterface.class).to(WPILibOperatorInterface.class);
        bind(ITimeProvider.class).to(WPILibTimeProvider.class);
    }
}
