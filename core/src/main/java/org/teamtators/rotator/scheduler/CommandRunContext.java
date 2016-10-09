package org.teamtators.rotator.scheduler;

public interface CommandRunContext {
    void cancelCommand(Command command) throws CommandException;

    default void startCommand(Command command) throws CommandException {
        startWithContext(command, this);
    }

    void startWithContext(Command command, CommandRunContext context) throws CommandException;
}
