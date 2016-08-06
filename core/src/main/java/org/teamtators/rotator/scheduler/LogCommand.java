package org.teamtators.rotator.scheduler;

public class LogCommand extends Command {
    private String message;

    public LogCommand(String name, String message) {
        super(name);
        this.message = message;
    }

    public LogCommand(String message) {
        this("LogCommand", message);
    }

    @Override
    protected void initialize() {
        logger.info(message);
    }

    @Override
    protected boolean step() {
        return true;
    }

    @Override
    protected void finish(boolean interrupted) {

    }
}
