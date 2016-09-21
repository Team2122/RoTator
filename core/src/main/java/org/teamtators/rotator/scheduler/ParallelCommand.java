package org.teamtators.rotator.scheduler;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class ParallelCommand extends Command implements CommandRunContext {

    private List<CommandRun> commands;

    public ParallelCommand(String name, Collection<Command> sequence) {
        super(name);
        setParallel(sequence);
    }

    public ParallelCommand(String name, Command... parallel) {
        this(name, Arrays.asList(parallel));
    }

    public ParallelCommand(Collection<Command> parallel) {
        this("ParallelCommand", parallel);
    }

    public ParallelCommand(Command... parallel) {
        this("ParallelCommand", Arrays.asList(parallel));
    }

    protected void setParallel(Collection<Command> commands) {
        checkNotNull(commands);

        // The valid states of a sequential command are the intersection of the valid states of the
        // child commands. So if a single command cannot run in Teleop, the whole sequential command
        // can not either.
        EnumSet<RobotState> validStates = EnumSet.allOf(RobotState.class);
        commands.forEach(command -> validStates.retainAll(command.getValidStates()));
        setValidStates(validStates);

        this.commands = commands.stream()
                .map(CommandRun::new)
                .collect(Collectors.toList());
    }

    @Override
    protected void initialize() {
        logger.debug("Parallel Command Initializing");
        for(CommandRun run : commands) {
            run.initialized = false;
            run.cancel = false;
        }
    }

    @Override
    protected boolean step() {
        if (commands.size() == 0) return true;
        for (CommandRun run : commands) {
            logger.trace("Parallel command running command {}", run.command.getName());
            if (run.cancel) {
                cancelRun(run);
            }
            if (!run.initialized) {
                if (run.command.isRunning()) {
                    if (run.command.getContext() == this && run.command.checkRequirements()) {
                        run.initialized = true;
                    } else {
                        run.command.cancel();
                    }
                } else if (run.command.startRun(this)) {
                    run.initialized = true;
                }
            }
            run.command.step();
            if (run.cancel) {
                cancelRun(run);
                return true;
            }
        }
        return false;
    }

    private void cancelRun(CommandRun run) {
        run.command.finishRun(true);
        getContext().cancelCommand(this);
    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted) {
            logger.debug("ParallelCommand interrupted");
        } else {
            logger.debug("ParallelCommand finished");

        }
    }

    @Override
    public void startCommand(Command command) {
        checkNotNull(command);
        if (getContext() == null)
            logger.debug("Tried to start command in parent execution context while ParallelCommand was not running");
        getContext().startCommand(command);
    }

    @Override
    public void cancelCommand(Command command) {
        checkNotNull(command);
        Optional<CommandRun> inGroup = findCommand(command);
        if (inGroup.isPresent()) {
            inGroup.get().cancel = true;
        } else {
            getContext().cancelCommand(command);
        }
    }

    private Optional<CommandRun> findCommand(Command command) {
        return commands.stream()
                .filter(run -> run.command == command)
                .findFirst();
    }
}
