package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.PickerPosition;

public class PickerCrossing extends CommandBase implements Configurable<PickerCrossing.Config> {
    private Config config;
    private AbstractPicker picker;

    public PickerCrossing(CoreRobot robot) {
        super("PickerCrossing");
        this.picker = robot.picker();
        requires(picker);
    }

    @Override
    protected boolean step() {
        PickerPosition targetPosition = PickerPosition.HOME;

        if (config.pickerPosition == PickerPosition.HOME) {
            targetPosition = PickerPosition.CHEVAL;
        } else if (config.pickerPosition == PickerPosition.CHEVAL) {
            targetPosition = PickerPosition.PICK;
        } else if (config.pickerPosition == PickerPosition.PICK) {
            targetPosition = PickerPosition.CHEVAL;
        }

        picker.setPosition(targetPosition);

        return true;
    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }

    static class Config {
        public PickerPosition pickerPosition;
    }
}
