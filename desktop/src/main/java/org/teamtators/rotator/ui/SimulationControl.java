package org.teamtators.rotator.ui;

import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.scheduler.Scheduler;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;

public class SimulationControl extends JPanel {
    private final JComboBox<RobotState> robotStateCombo;

    @Inject
    public SimulationControl(Scheduler scheduler) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(new Label("Robot State: "));
        robotStateCombo = new JComboBox<>();
        robotStateCombo.setModel(new SchedulerRobotStateModel(scheduler));
        this.add(robotStateCombo);
    }

    private static class SchedulerRobotStateModel implements ComboBoxModel<RobotState> {
        private final Scheduler scheduler;

        public SchedulerRobotStateModel(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public Object getSelectedItem() {
            return scheduler.getRobotState();
        }

        @Override
        public void setSelectedItem(Object anItem) {
            scheduler.enterState((RobotState) anItem);
        }

        @Override
        public int getSize() {
            return RobotState.values().length;
        }

        @Override
        public RobotState getElementAt(int index) {
            return RobotState.values()[index];
        }

        @Override
        public void addListDataListener(ListDataListener l) {

        }

        @Override
        public void removeListDataListener(ListDataListener l) {

        }
    }
}
