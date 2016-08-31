package org.teamtators.rotator.operatorInterface;

import com.google.inject.Singleton;
import org.teamtators.rotator.config.Configurable;

@Singleton
public class WPILibOperatorInterface
        extends AbstractOperatorInterface
        implements Configurable<WPILibOperatorInterface.Config> {
    private WPILibLogitechF310 driverJoystick;

    @Override
    public void configure(Config config) {
        logger.trace("Configuring operator interface");
        driverJoystick = new WPILibLogitechF310(config.driverJoystick);
    }

    @Override
    public LogitechF310 driverJoystick() {
        return driverJoystick;
    }

    public static class Config {
        public int driverJoystick;
    }
}
