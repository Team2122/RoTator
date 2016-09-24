package org.teamtators.rotator.scheduler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParallelCommand extends Command implements CommandRunContext {
    private List<CommandRun> running = new ArrayList<>();

    public ParallelCommand(Command... commands) {
        super("ParallelCommand");
        for(int i = 0; i < commands.length; i++) {
            running.add(new CommandRun(commands[i]));
        }
    }

    @Override
    protected void initialize() {
        for(CommandRun run : running) {
            startRun(this);
            run.initialized = true;
        }
    }

    @Override
    protected boolean step() {
        Iterator<CommandRun> it = running.iterator();
        while(it.hasNext()) {
            CommandRun run = it.next();
            if(run.cancel || run.command.step()) {
                run.command.finishRun(run.cancel);
                it.remove();
            }
        }
        return running.size() < 1;
    }

    @Override
    protected void finish(boolean interrupted) {
        running.forEach(run -> run.command.finishRun(interrupted));
    }

    @Override
    public void cancelCommand(Command command) {
        for(CommandRun run : running) {
            if(run.command == command) {
                run.cancel = true;
            }
        }
    }

    @Override
    public void startCommand(Command command) {
        throw new UnsupportedOperationException("The parallel command ship has sailed...");
    }
}
