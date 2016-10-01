package org.teamtators.rotator.ui;

import org.teamtators.rotator.components.Chooser;

import javax.inject.Inject;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SimulationChooser<T> extends JPanel implements Chooser<T> {
    private JComboBox<String> comboBox;
    private Map<String, T> optionMap = new HashMap<>();

    @Inject
    public SimulationChooser(SimulationControl controlBar) {
        add(new JLabel("Auto:"));
        comboBox = new JComboBox<>();
        add(comboBox);
        controlBar.add(this);
    }

    @Override
    public T getSelected() {
        return optionMap.get(comboBox.getSelectedItem());
    }

    @Override
    public void registerOption(String name, T option) {
        optionMap.put(name, option);
        comboBox.addItem(name);
        System.out.println("Registering "+name);
    }

    @Override
    public void registerDefault(String name, T option) {
        registerOption(name, option);
        comboBox.setSelectedItem(name);
    }
}
