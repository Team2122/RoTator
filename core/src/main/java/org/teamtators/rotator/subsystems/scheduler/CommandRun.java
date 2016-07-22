package org.teamtators.rotator.subsystems.scheduler;

class CommandRun {
    CommandRun(Command command) {
        this.command = command;
    }

    Command command;
    boolean initialized = false;
    boolean cancel = false;
}
