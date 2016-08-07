package org.teamtators.rotator.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Command {
    protected Logger logger;
    private String name;
    private CommandRunContext context = null;
    private Set<Subsystem> requirements = null;

    public Command(String name) {
        checkNotNull(name);
        setName(name);
    }

    protected abstract void initialize();
    protected abstract boolean step();
    protected abstract void finish(boolean interrupted);

    public void setName(String name) {
        this.name = name;
        String loggerName = String.format("%s(%s)", this.getClass().getName(), name);
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    public String getName() {
        return name;
    }

    CommandRunContext getContext() {
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

    protected void requires(Subsystem subsystem) {
        if (requirements == null) {
            requirements = new HashSet<>();
        }
        requirements.add(subsystem);
    }

    Set<Subsystem> getRequirements() {
        return requirements;
    }

    public boolean doesRequire(Subsystem subsystem) {
        return requirements != null && requirements.contains(subsystem);
    }

    public static Command oneShot(Runnable function) {
        return new OneShotCommand(function);
    }

    public static Command sequence(Command... sequence) {
        return new SequentialCommand(sequence);
    }

    public static Command log(String message) {
        return new LogCommand(message);
    }
}
