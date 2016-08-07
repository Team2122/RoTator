package org.teamtators.rotator.commands;

import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Command;

public class LogCommand extends Command implements Configurable<LogCommand.Config> {
    static class Config {
        public String message;
    }

    private Config config;

    public LogCommand() {
        super("LogCommand");
    }

    @Override
    protected void initialize() {
        logger.info(config.message);
    }

    @Override
    protected boolean step() {
        return true;
    }

    @Override
    protected void finish(boolean interrupted) {

    }

    @Override
    public void configure(Config config) {
        this.config = config;
    }
}
