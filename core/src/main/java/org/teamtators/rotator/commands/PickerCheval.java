package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.PickerPosition;

public class PickerCheval extends CommandBase{
    AbstractPicker picker;

    public PickerCheval(CoreRobot robot) {
        super("PickerCheval");
        this.picker = robot.picker();
        requires(picker);
    }

    @Override
    protected void initialize() {
        picker.setPosition(PickerPosition.CHEVAL);
    }

    @Override
    protected boolean step() {
        if(picker.isAtCheval()) {
            picker.setPosition(PickerPosition.PICK);
            return true;
        } else {
            return false;
        }
    }
}
