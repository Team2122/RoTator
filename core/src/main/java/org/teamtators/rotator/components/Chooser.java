package org.teamtators.rotator.components;

public interface Chooser<T> {
    T getSelected();
    void registerOption(String name, T option);
    void registerDefault(String name, T option);
}
