package org.teamtators.rotator.subsystems.scheduler;

public class LogCommand extends Command {
    private String message;

    public LogCommand(String message) {
        super("LogCommand");
        this.message = message;
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
