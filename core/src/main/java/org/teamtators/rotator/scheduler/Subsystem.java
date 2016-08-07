package org.teamtators.rotator.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Subsystem {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String name;

    private Command requiringCommand = null;
    private Command defaultCommand = null;

    public Subsystem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    Command getRequiringCommand() {
        return requiringCommand;
    }

    void setRequiringCommand(Command requiringCommand) {
        this.requiringCommand = requiringCommand;
    }

    Command getDefaultCommand() {
        return defaultCommand;
    }

    public void setDefaultCommand(Command defaultCommand) {
        this.defaultCommand = defaultCommand;
    }
}
