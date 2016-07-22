package org.teamtators.rotator.subsystems.scheduler;

public interface CommandRunContext {
    void cancelCommand(Command command) throws CommandException;
    void startCommand(Command command) throws CommandException;
}
