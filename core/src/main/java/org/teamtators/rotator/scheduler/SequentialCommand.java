package org.teamtators.rotator.scheduler;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class SequentialCommand extends Command implements CommandRunContext {
    private List<CommandRun> sequence;
    private int currentPosition;

    public SequentialCommand(String name, Collection<Command> sequence) {
        super(name);
        setSequence(sequence);
    }

    public SequentialCommand(String name, Command... sequence) {
        this(name, Arrays.asList(sequence));
    }

    public SequentialCommand(Collection<Command> sequence) {
        this("SequentialCommand", sequence);
    }

    public SequentialCommand(Command... sequence) {
        this("SequentialCommand", Arrays.asList(sequence));
    }

    private CommandRun currentRun() {
        return sequence.get(currentPosition);
    }

    protected void setSequence(Collection<Command> sequence) {
        checkNotNull(sequence);

        // The valid states of a sequential command are the intersection of the valid states of the
        // child commands. So if a single command cannot run in Teleop, the whole sequential command
        // can not either.
        EnumSet<RobotState> validStates = EnumSet.allOf(RobotState.class);
        sequence.forEach(command -> validStates.retainAll(command.getValidStates()));
        setValidStates(validStates);

        this.sequence = sequence.stream()
                .map(CommandRun::new)
                .collect(Collectors.toList());
    }

    @Override
    protected void initialize() {
        logger.debug("SequentialCommand initializing");
        for (CommandRun run : sequence) {
            run.initialized = false;
            run.cancel = false;
        }
        currentPosition = 0;
    }

    @Override
    protected boolean step() {
        if (sequence.size() == 0) return true;
        boolean finished;
        do {
            CommandRun run = currentRun();
            if (run.cancel) {
                cancelRun(run);
                return true;
            }
            if (!run.initialized) {
                if (run.command.isRunning()) {
                    if (run.command.getContext() == this && run.command.checkRequirements()) {
                        run.initialized = true;
                    } else {
                        run.command.cancel();
                        return false;
                    }
                } else if (run.command.startRun(this)) {
                    run.initialized = true;
                }
            }
            finished = run.command.step();
            if (run.cancel) {
                cancelRun(run);
                return true;
            }
            if (finished) {
                run.command.finishRun(false);
                currentPosition++;
                if (currentPosition >= sequence.size()) {
                    logger.trace("Sequential command finished");
                    return true;
                }
                logger.trace("Sequential command advancing to command {}", currentPosition);
            }
        } while (finished);
        return false;
    }

    private void cancelRun(CommandRun run) {
        run.command.finishRun(true);
        getContext().cancelCommand(this);
    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted) {
            logger.debug("SequentialCommand interrupted");
        } else {
            logger.debug("SequentialCommand finished");
        }
        if (sequence.size() == 0 || currentPosition >= sequence.size()) return;
        Command currentCommand = currentRun().command;
        if (interrupted && currentCommand.isRunning()) {
            currentCommand.finishRun(true);
        }
    }

    @Override
    public void startCommand(Command command) {
        checkNotNull(command);
        if (getContext() == null)
            logger.debug("Tried to start command in parent execution context while SequentialCommand was not running");
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
        return sequence.stream()
                .filter(run -> run.command == command)
                .findFirst();
    }
}
