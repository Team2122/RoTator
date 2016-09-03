package org.teamtators.rotator.config;

import org.teamtators.rotator.operatorInterface.LogitechF310;

import java.util.Map;

public class TriggersConfig {
    private Map<LogitechF310.Button, String> driverButtons;
    private Map<LogitechF310.Button, String> gunnerButtons;

    public Map<LogitechF310.Button, String> getDriverButtons() {
        return driverButtons;
    }

    public void setDriverButtons(Map<LogitechF310.Button, String> driverButtons) {
        this.driverButtons = driverButtons;
    }

    public Map<LogitechF310.Button, String> getGunnerButtons() {
        return gunnerButtons;
    }

    public void setGunnerButtons(Map<LogitechF310.Button, String> gunnerButtons) {
        this.gunnerButtons = gunnerButtons;
    }
}
