package org.teamtators.rotator;

import edu.wpi.first.wpilibj.command.Command;

/**
 * All commands inherit from this
 */
public abstract class CommandBase extends Command {

    @Override
    protected void initialize() {
         //Todo: add logging
    }

    @Override
    protected void interrupted() {
        //Todo: add logging
        finish(true);
    }

    @Override
    protected void end() {
        //Todo: add logging
        finish(false);
    }

    protected abstract void finish(boolean wasInterrupted);
}
