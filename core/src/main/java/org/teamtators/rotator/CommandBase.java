package org.teamtators.rotator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teamtators.rotator.scheduler.Command;

/**
 * All commands inherit from this
 */
public abstract class CommandBase extends Command {
    private Logger logger = LogManager.getLogger(this.getClass());

    public CommandBase(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        logger.info("Command initializing");
    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted) {
            logger.info("Command interrupted");
        } else {
            logger.info("Command ending");
        }
    }

}
