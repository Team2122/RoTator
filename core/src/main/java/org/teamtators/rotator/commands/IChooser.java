package org.teamtators.rotator.commands;

public interface IChooser<T> {
    T getSelected();
    void registerOption(String name, T option);
}
