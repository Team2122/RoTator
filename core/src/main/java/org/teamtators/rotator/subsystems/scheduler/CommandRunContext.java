package org.teamtators.rotator.subsystems.scheduler;

/**
 * Created by alex on 7/14/16.
 */
public interface CommandRunContext {
    void cancelCommand(Command command) throws CommandException;
    void startCommand(Command command) throws CommandException;
}
