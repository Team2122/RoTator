package org.teamtators.rotator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.control.SystemNanoTimeTimeProvider;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.operatorInterface.SimulationOperatorInterface;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.subsystems.*;
import org.teamtators.rotator.subsystems.noop.NoopTurret;
import org.teamtators.rotator.subsystems.noop.NoopVision;
import org.teamtators.rotator.ui.WASDJoystick;

import java.util.Arrays;
import java.util.List;

public class DesktopModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CoreModule()
                .withConfigDir("./config"));
        bind(AbstractDrive.class).to(SimulationDrive.class);
        bind(AbstractPicker.class).to(SimulationPicker.class);
        bind(AbstractTurret.class).to(SimulationTurret.class);
        bind(AbstractVision.class).to(NoopVision.class);
        bind(LogitechF310.class).to(WASDJoystick.class);
        bind(ITimeProvider.class).to(SystemNanoTimeTimeProvider.class);
    }

    @Provides
    @Singleton
    public AbstractOperatorInterface providesOperatorInterface(LogitechF310 driverJoystick, LogitechF310 gunnerJoystick) {
        return new SimulationOperatorInterface(driverJoystick, gunnerJoystick);
    }
}
