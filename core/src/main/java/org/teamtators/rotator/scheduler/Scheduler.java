package org.teamtators.rotator.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Scheduler implements CommandRunContext {
    private static Logger logger = LogManager.getLogger(Scheduler.class);

    private Map<String, CommandRun> runningCommands = new ConcurrentHashMap<>();

    public void execute() {
        logger.trace("{} commands running", runningCommands.size());
        for (CommandRun run : runningCommands.values()) {
            if (run.cancel) {
                run.command.finish(true);
                finishRun(run);
                continue;
            } else if (!run.initialized) {
                if (run.command.isRunning()) continue;
                run.command.setContext(this);
                run.command.initialize();
                run.initialized = true;
            }
            boolean finished = run.command.step();
            if (finished || run.cancel) {
                run.command.finish(run.cancel);
                finishRun(run);
            }
        }
    }

    private void finishRun(CommandRun run) {
        run.command.setContext(null);
        runningCommands.remove(run.command.getName());
    }

    @Override
    public void startCommand(Command command) {
        checkNotNull(command);
        CommandRun run = runningCommands.get(command.getName());
        if (run != null)
            return;
        if (command.getContext() != null) {
            command.cancel();
        }
        runningCommands.put(command.getName(), new CommandRun(command));
    }

    public void cancelCommand(String commandName) {
        checkNotNull(commandName);
        if (!runningCommands.containsKey(commandName))
            throw new CommandException("Cannot cancel command that is not running on this scheduler");
        CommandRun run = runningCommands.get(commandName);
        run.cancel = true;
    }

    @Override
    public void cancelCommand(Command command) {
        checkNotNull(command);
        cancelCommand(command.getName());
    }
}
