package org.teamtators.rotator.components;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WPILibChooser<T> implements Chooser<T> {
    private SendableChooser chooser;
    private String name;

    public WPILibChooser(String name) {
        chooser = new SendableChooser();
        SmartDashboard.putData(name, chooser);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getSelected() {
        return (T) chooser.getSelected();
    }

    @Override
    public void registerOption(String name, T option) {
        chooser.addObject(name, option);
    }

    @Override
    public void registerDefault(String name, T option) {
        chooser.addDefault(name, option);
    }
}
