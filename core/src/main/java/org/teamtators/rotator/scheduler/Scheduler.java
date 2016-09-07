package org.teamtators.rotator.scheduler;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public final class Scheduler implements CommandRunContext {
    private static Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private Map<TriggerSource, List<TriggerScheduler>> triggerSchedulers = new HashMap<>();
    private Map<String, CommandRun> runningCommands = new ConcurrentHashMap<>();
    private Set<Subsystem> subsystems = new HashSet<>();
    private Set<Command> defaultCommands = new HashSet<>();

    private RobotState robotState = RobotState.DISABLED;

    public void registerSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    public void registerSubsystems(Collection<Subsystem> subsystems) {
        this.subsystems.addAll(subsystems);
    }

    public void clearSubsystems() {
        this.subsystems.clear();
    }

    public void registerDefaultCommand(Command defaultCommand) {
        defaultCommands.add(defaultCommand);
    }

    public void registerDefaultCommands(Collection<Command> defaultCommands) {
        this.defaultCommands.addAll(defaultCommands);
    }

    public void clearDefaultCommands() {
        defaultCommands.clear();
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
//        logger.trace("Scheduler in state {}, {} triggers, {} commands", robotState, triggerSchedulers.size(),
//                runningCommands.size());
        for (Map.Entry<TriggerSource, List<TriggerScheduler>> entry : triggerSchedulers.entrySet()) {
            TriggerSource triggerSource = entry.getKey();
            boolean active = triggerSource.getActive();
            for (TriggerScheduler scheduler : entry.getValue()) {
                scheduler.processTrigger(active);
            }
        }
        for (CommandRun run : runningCommands.values()) {
            if (run.cancel) {
                logger.trace("Cancelling command {} by request", run.command.getName());
                finishRun(run, true);
                continue;
            } else if (!run.command.isValidInState(robotState)) {
                logger.trace("Cancelling command {} because of state conflict in {}", run.command.getName(),
                        robotState);
                finishRun(run, true);
                continue;
            } else if (!run.initialized) {
                if (!run.command.startRun(this)) {
                    logger.trace("Command {} not ready to run yet because of requirements", run.command.getName());
                    continue;
                }
                run.initialized = true;
            }
            boolean finished = run.command.step();
            if (finished || run.cancel) {
                logger.trace("Command {} finished, it was cancelled?: {}", run.command.getName(), run.cancel);
                finishRun(run, run.cancel);
            }
        }
        for (Command command : defaultCommands) {
            if (command.checkRequirements()
                    && command.isValidInState(robotState)
                    && !command.isRunning()) {
                startCommand(command);
            }
        }
    }

    private void finishRun(CommandRun run, boolean cancelled) {
        run.command.finishRun(cancelled);
        runningCommands.remove(run.command.getName());
    }

    @Override
    public void startCommand(Command command) {
        checkNotNull(command);
        CommandRun run = runningCommands.get(command.getName());
        if (run != null || !command.isValidInState(robotState))
            return;
        if (command.getContext() != null) {
            command.cancel();
        }
        runningCommands.put(command.getName(), new CommandRun(command));
    }

    public void cancelCommand(String commandName) {
        checkNotNull(commandName);
        CommandRun run = runningCommands.get(commandName);
        if (run == null)
            logger.debug("Attempted to cancel not command that was not running: {}", commandName);
        else
            run.cancel = true;
    }

    @Override
    public void cancelCommand(Command command) {
        checkNotNull(command);
        cancelCommand(command.getName());
    }

    public RobotState getRobotState() {
        return robotState;
    }

    public void enterState(RobotState currentState) {
        this.robotState = currentState;
        switch (currentState) {
            case DISABLED:
                logger.info("Robot disabled");
                break;
            case AUTONOMOUS:
                logger.info("Robot enabled in autonomous");
                break;
            case TELEOP:
                logger.info("Robot enabled in teleop");
                break;
            case TEST:
                logger.info("Robot enabled in test");
                break;
        }
    }
}
