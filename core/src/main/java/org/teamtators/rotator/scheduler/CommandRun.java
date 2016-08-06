package org.teamtators.rotator.scheduler;

class CommandRun {
    CommandRun(Command command) {
        this.command = command;
    }

    Command command;
    boolean initialized = false;
    boolean cancel = false;
}
