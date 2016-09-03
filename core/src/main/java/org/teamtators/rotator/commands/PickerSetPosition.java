package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.PickerPosition;

import javax.inject.Inject;

public class PickerSetPosition extends CommandBase {
    private AbstractPicker picker;

    public static class Config {
        public PickerPosition pickerPosition;
    }

    public PickerPosition pickerPosition;

    public void configure(Config config) {
        this.pickerPosition = config.pickerPosition;
    }

    @Inject
    public PickerSetPosition(AbstractPicker picker) {
        super("PickerSetPosition");
        this.picker = picker;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected boolean step() {
        picker.setPosition(pickerPosition);
        return true;
    }
}
