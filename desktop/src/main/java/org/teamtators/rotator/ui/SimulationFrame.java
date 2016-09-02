package org.teamtators.rotator.ui;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {
    @Inject
    public SimulationFrame(SimulationDisplay display, SimulationControl control) throws HeadlessException {
        super("RoTator Simulation");

        this.setLayout(new BorderLayout());
        this.add(display);
        display.requestFocusInWindow();
        this.add(control, BorderLayout.SOUTH);

        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
