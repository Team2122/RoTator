package org.teamtators.rotator.commands;

import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.PickerPosition;

import static org.teamtators.rotator.subsystems.PickerPosition.*;

public class PickerCrossing extends CommandBase {
    private AbstractPicker picker;

    public PickerCrossing(CoreRobot robot) {
        super("PickerCrossing");
        this.picker = robot.picker();
        requires(picker);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected boolean step() {

        PickerPosition pickerPosition = picker.getPosition();
        PickerPosition targetPosition = HOME;
        PickerPosition originalPosition = HOME;

        switch (pickerPosition) {
            case HOME:
                targetPosition = CHEVAL;
                originalPosition = HOME;
            case PICK:
                targetPosition = CHEVAL;
                originalPosition = PICK;
            case CHEVAL:
                targetPosition = PICK;
                originalPosition = CHEVAL;
        }

        logger.info("Moving picker from {} to {}", originalPosition, targetPosition);

        picker.setPosition(targetPosition);

        return true;
    }

    @Override
    protected void finish(boolean interrupted) {
    }

}
