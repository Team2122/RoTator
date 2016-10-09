package org.teamtators.rotator.scheduler;

class CommandRun {
    Command command;
    boolean initialized = false;
    boolean cancel = false;
    CommandRunContext context = null;

    CommandRun(Command command) {
        this.command = command;
    }
}
