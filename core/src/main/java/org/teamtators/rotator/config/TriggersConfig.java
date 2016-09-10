package org.teamtators.rotator.config;

import org.teamtators.rotator.operatorInterface.LogitechF310;

import java.util.Map;

public class TriggersConfig {
    private Map<LogitechF310.Button, String> driver;
    private Map<LogitechF310.Button, String> gunner;

    public Map<LogitechF310.Button, String> getDriver() {
        return driver;
    }

    public void setDriver(Map<LogitechF310.Button, String> driver) {
        this.driver = driver;
    }

    public Map<LogitechF310.Button, String> getGunner() {
        return gunner;
    }

    public void setGunner(Map<LogitechF310.Button, String> gunner) {
        this.gunner = gunner;
    }
}
