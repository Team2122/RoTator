package org.teamtators.rotator.subsystems.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Command {
    protected final Logger logger;
    private final String name;
    private CommandRunContext context = null;

    public Command(String name) {
        checkNotNull(name);
        this.name = name;
        String loggerName = String.format("%s(%s)", this.getClass().getName(), name);
        this.logger = LogManager.getLogger(loggerName);
    }

    protected abstract void initialize();
    protected abstract boolean step();
    protected abstract void finish(boolean interrupted);

    public String getName() {
        return name;
    }

    public CommandRunContext getContext() {
        return context;
    }

    void setContext(CommandRunContext context) {
        this.context = context;
    }

    public boolean isRunning() {
        return this.context != null;
    }

    public void cancel() {
        if (this.context == null) {
            throw new IllegalStateException("Tried to cancel command that is not running");
        }
        this.context.cancelCommand(this);
    }

    public static Command oneShot(String name, Runnable function) {
        return new OneShotCommand(name, function);
    }

    public static Command sequence(String name, Command... sequence) {
        return new SequentialCommand(name, sequence);
    }

    public static Command log(String message) {
        return new LogCommand(message);
    }
}
