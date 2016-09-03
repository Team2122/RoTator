package org.teamtators.rotator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.control.SystemNanoTimeTimeProvider;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.operatorInterface.SimulationOperatorInterface;
import org.teamtators.rotator.subsystems.AbstractDrive;
import org.teamtators.rotator.subsystems.SimulationDrive;
import org.teamtators.rotator.ui.WASDJoystick;

public class DesktopModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CoreModule()
                .withConfigDir("./config"));
        bind(AbstractDrive.class).to(SimulationDrive.class);
        bind(LogitechF310.class).to(WASDJoystick.class);
        bind(ITimeProvider.class).to(SystemNanoTimeTimeProvider.class);
    }

    @Provides @Singleton
    public AbstractOperatorInterface providesOperatorInterface(LogitechF310 driverJoystick, LogitechF310 gunnerJoystick) {
        return new SimulationOperatorInterface(driverJoystick, gunnerJoystick);
    }
}
