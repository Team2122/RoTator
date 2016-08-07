package org.teamtators.rotator;

import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.subsystems.AbstractDrive;

/**
 * All commands inherit from this
 */
public abstract class CommandBase extends Command {
    public CommandBase(String name) {
        super(name);
    }

    public static void setScheduler(Scheduler scheduler) {
        CommandBase.scheduler = scheduler;
    }

    public static void setCommandStore(CommandStore commandStore) {
        CommandBase.commandStore = commandStore;
    }

    public static void setDrive(AbstractDrive drive) {
        CommandBase.drive = drive;
    }

    public static void setDriverJoystick(ILogitechF310 driverJoystick) {
        CommandBase.driverJoystick = driverJoystick;
    }

    protected static Scheduler scheduler;
    protected static CommandStore commandStore;
    protected static AbstractDrive drive;
    protected static ILogitechF310 driverJoystick;

    @Override
    protected void initialize() {
        logger.info("{} initializing", getName());
    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted) {
            logger.info("{} interrupted", getName());
        } else {
            logger.info("{} ended", getName());
        }
    }

}
