package org.teamtators.rotator;

import org.teamtators.rotator.scheduler.Command;

/**
 * All commands inherit from this
 */
public abstract class CommandBase extends Command {
    public CommandBase(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        logger.info("{} initializing", getName());
    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted) {
            logger.info("{} interrupted", getName());
        } else {
            logger.info("{} ended", getName());
        }
    }

}
