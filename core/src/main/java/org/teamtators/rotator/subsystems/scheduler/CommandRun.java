package org.teamtators.rotator.subsystems.scheduler;

/**
 * Created by alex on 7/21/16.
 */
class CommandRun {
    CommandRun(Command command) {
        this.command = command;
    }

    Command command;
    boolean initialized = false;
    boolean cancel = false;
}
