package org.teamtators.rotator.commands;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WPILibChooser<T> implements IChooser<T> {
    private SendableChooser chooser;
    private String name;

    @Override
    @SuppressWarnings("unchecked")
    public T getSelected() {
        return (T) chooser.getSelected();
    }

    @Override
    public void registerOption(String name, T option) {
        chooser.addObject(name, option);
    }

    public WPILibChooser(String name) {
        chooser = new SendableChooser();
        SmartDashboard.putData(name, chooser);
    }
}
