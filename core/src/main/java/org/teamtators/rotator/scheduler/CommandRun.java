package org.teamtators.rotator.scheduler;

class CommandRun implements ICommandRun {
    Command command;
    boolean initialized = false;
    boolean cancel = false;

    CommandRun(Command command) {
        this.command = command;
    }

    @Override
    public Command getCommand() {
        return command;
    }
}
