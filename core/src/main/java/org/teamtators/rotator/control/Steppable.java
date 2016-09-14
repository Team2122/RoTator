package org.teamtators.rotator.control;

public interface Steppable {
    default void onEnable() {
    }

    void step(double delta);

    default void onDisable() {
    }
}
