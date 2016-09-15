package org.teamtators.rotator.operatorInterface;

import org.teamtators.rotator.config.Configurable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WPILibOperatorInterface
        extends AbstractOperatorInterface
        implements Configurable<WPILibOperatorInterface.Config> {
    private WPILibLogitechF310 driverJoystick;
    private WPILibLogitechF310 gunnerJoystick;

    @Inject
    public WPILibOperatorInterface() {
    }

    @Override
    public void configure(Config config) {
        logger.trace("Configuring operator interface");
        driverJoystick = new WPILibLogitechF310(config.driverJoystick);
        gunnerJoystick = new WPILibLogitechF310(config.gunnerJoystick);
    }

    @Override
    public LogitechF310 driverJoystick() {
        return driverJoystick;
    }

    @Override
    public LogitechF310 gunnerJoystick() {
        return gunnerJoystick;
    }

    public static class Config {
        public int driverJoystick;
        public int gunnerJoystick;
    }
}
