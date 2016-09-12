package org.teamtators.rotator.scheduler;

class CommandRun {
    Command command;
    boolean initialized = false;
    boolean cancel = false;

    CommandRun(Command command) {
        this.command = command;
    }
}
