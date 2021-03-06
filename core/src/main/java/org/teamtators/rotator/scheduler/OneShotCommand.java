package org.teamtators.rotator.scheduler;

public class OneShotCommand extends Command {
    private Runnable function;

    public OneShotCommand(String name, Runnable function) {
        super(name);
        this.function = function;
    }

    public OneShotCommand(Runnable function) {
        this("OneShotCommand", function);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected boolean step() {
        function.run();
        return true;
    }

    @Override
    protected void finish(boolean interrupted) {
    }
}
