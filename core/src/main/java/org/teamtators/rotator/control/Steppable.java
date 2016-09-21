package org.teamtators.rotator.control;

public interface Steppable {
    default void onEnable() {
    }

    void step(double delta);

    default void onDisable() {
    }

    /**
     * Gets an integer that determines what order this Steppable will execute in.
     * Default is 100. Controllers run at 200 by default. Simulation subsystems run at 300
     * Log data collectors run at 400
     * @return
     */
    default int getExecutionOrder() {
        return 100;
    }
}
