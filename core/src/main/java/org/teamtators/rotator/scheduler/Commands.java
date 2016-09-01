package org.teamtators.rotator.scheduler;

public class Commands {
    private static int nextLogCommandNumber = 1;

    public static Command oneShot(Runnable function) {
        return new OneShotCommand(function);
    }

    public static Command sequence(Command... sequence) {
        return new SequentialCommand(sequence);
    }

    public static Command log(String message) {
        return new LogCommand("LogCommand" + nextLogCommandNumber++, message);
    }
}
