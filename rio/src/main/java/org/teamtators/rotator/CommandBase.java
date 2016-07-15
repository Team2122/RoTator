package org.teamtators.rotator;

import edu.wpi.first.wpilibj.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * All commands inherit from this
 */
public abstract class CommandBase extends Command {
    private Logger logger = LogManager.getLogger(this.getClass());

    @Override
    protected void initialize() {
        logger.info("Command initializing");
    }

    @Override
    protected void interrupted() {
        logger.info("Command interrupted");
        finish(true);
    }

    @Override
    protected void end() {
        logger.info("Command ending");
        finish(false);
    }

    protected abstract void finish(boolean wasInterrupted);
}
