package org.teamtators.rotator.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Command {
    protected Logger logger;
    private String name;
    private CommandRunContext context = null;
    private Set<Subsystem> requirements = null;
    private EnumSet<RobotState> validStates = EnumSet.of(RobotState.AUTONOMOUS, RobotState.TELEOP);

    public Command(String name) {
        checkNotNull(name);
        setName(name);
    }

    protected abstract void initialize();

    protected abstract boolean step();

    protected abstract void finish(boolean interrupted);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        String loggerName = String.format("%s(%s)", this.getClass().getName(), name);
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    CommandRunContext getContext() {
        return context;
    }

    private void setContext(CommandRunContext context) {
        this.context = context;
    }

    public boolean isRunning() {
        return this.context != null;
    }

    protected void startCommand(Command command) {
        if (this.context == null) {
            throw new IllegalStateException("Tried add command in parent context while not running");
        }
        this.context.startCommand(command);
    }

    protected void cancelCommand(Command command) {
        if (this.context == null || command.getContext() == null) {
            throw new IllegalStateException("Tried to cancel command that is not running");
        }
        this.context.cancelCommand(command);
    }

    public void cancel() {
        this.cancelCommand(this);
    }

    protected void requires(Subsystem subsystem) {
        checkNotNull(subsystem, "Cannot require a null subsystem");
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

    public boolean checkRequirements() {
        if (requirements == null)
            return true;
        for (Subsystem subsystem : requirements) {
            Command requiringCommand = subsystem.getRequiringCommand();
            if (requiringCommand != null && requiringCommand != this)
                return false;
        }
        return true;
    }

    protected boolean takeRequirements(Iterable<Subsystem> requirements) {
        boolean anyRequiring = false;
        for (Subsystem subsystem : requirements) {
            Command requiringCommand = subsystem.getRequiringCommand();
            if (requiringCommand != null && requiringCommand != this) {
                anyRequiring = true;
                requiringCommand.cancel();
            } else {
                subsystem.setRequiringCommand(this);
            }
        }
        return !anyRequiring;
    }

    protected boolean takeRequirements(Subsystem... requirements) {
        return takeRequirements(Arrays.asList(requirements));
    }

    protected boolean takeRequirements() {
        return this.requirements == null || takeRequirements(this.requirements);
    }

    boolean startRun(CommandRunContext context) {
        if (isRunning() || !takeRequirements()) return false;
        setContext(context);
        initialize();
        return true;
    }

    void finishRun(boolean cancelled) {
        if (isRunning()) {
            finish(cancelled);
            setContext(null);
        }
        releaseRequirements();
    }

    private void releaseRequirements() {
        if (requirements == null)
            return;
        for (Subsystem subsystem : requirements) {
            subsystem.setRequiringCommand(null);
        }
    }

    public boolean isValidInState(RobotState state) {
        return validStates.contains(state);
    }

    protected void validIn(RobotState... states) {
        setValidStates(EnumSet.copyOf(Arrays.asList(states)));
    }

    public EnumSet<RobotState> getValidStates() {
        return validStates;
    }

    protected void setValidStates(EnumSet<RobotState> validStates) {
        this.validStates = validStates;
    }
}
