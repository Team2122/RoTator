package org.teamtators.rotator.scheduler;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public final class Scheduler implements CommandRunContext {
    private static Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private Map<TriggerSource, List<TriggerScheduler>> triggerSchedulers = new HashMap<>();
    private Map<String, CommandRun> runningCommands = new ConcurrentHashMap<>();
    private Set<Subsystem> subsystems = new HashSet<>();

    public void registerSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    public void registerSubsystems(Collection<Subsystem> subsystems) {
        this.subsystems.addAll(subsystems);
    }

    public void addTrigger(TriggerSource source, TriggerScheduler scheduler) {
        List<TriggerScheduler> schedulers = triggerSchedulers.get(source);
        if (schedulers == null) {
            triggerSchedulers.put(source, new ArrayList<>(Collections.singletonList(scheduler)));
        } else {
            schedulers.add(scheduler);
        }
    }

    public TriggerAdder onTrigger(TriggerSource triggerSource) {
        return new TriggerAdder(this, triggerSource);
    }

    public void execute() {
        for (Map.Entry<TriggerSource, List<TriggerScheduler>> entry : triggerSchedulers.entrySet()) {
            TriggerSource triggerSource = entry.getKey();
            boolean active = triggerSource.getActive();
            for (TriggerScheduler scheduler : entry.getValue()) {
                scheduler.processTrigger(active);
            }
        }
        logger.trace("{} commands running", runningCommands.size());
        for (CommandRun run : runningCommands.values()) {
            if (run.cancel) {
                run.command.finish(true);
                finishRun(run);
                continue;
            } else if (!run.initialized) {
                if (run.command.isRunning() || !takeRequirements(run.command)) continue;
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
        for (Subsystem subsystem : subsystems) {
            if (subsystem.getRequiringCommand() == null && subsystem.getDefaultCommand() != null) {
                startCommand(subsystem.getDefaultCommand());
            }
        }
    }


    @Override
    public boolean takeRequirements(Command command) {
        if (command.getRequirements() == null)
            return true;
        boolean anyRequiring = false;
        for (Subsystem subsystem : command.getRequirements()) {
            Command requiringCommand = subsystem.getRequiringCommand();
            if (requiringCommand != null && requiringCommand != command) {
                anyRequiring = true;
                requiringCommand.cancel();
            } else {
                subsystem.setRequiringCommand(command);
            }
        }
        return !anyRequiring;
    }

    @Override
    public void releaseRequirements(Command command) {
        if (command.getRequirements() == null)
            return;
        for (Subsystem subsystem : command.getRequirements()) {
            subsystem.setRequiringCommand(null);
        }
    }

    private void finishRun(CommandRun run) {
        releaseRequirements(run.command);
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
