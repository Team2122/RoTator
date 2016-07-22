package org.teamtators.rotator.subsystems.scheduler;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class SequentialCommand extends Command implements CommandRunContext {
    private List<CommandRun> sequence;
    private int currentPosition;

    public SequentialCommand(String name, Collection<Command> sequence) {
        super(name);
        checkNotNull(sequence);
        this.sequence = sequence.stream()
                .map(CommandRun::new)
                .collect(Collectors.toList());
    }

    public SequentialCommand(String name, Command[] sequence) {
        this(name, Arrays.asList(sequence));
    }

    protected CommandRun currentRun() {
        return sequence.get(currentPosition);
    }

    @Override
    protected void initialize() {
        logger.debug("initialize");
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
                    if (run.command.getContext() == this) {
                        run.initialized = true;
                    } else {
                        run.command.cancel();
                        return false;
                    }
                } else {
                    run.command.setContext(this);
                    run.command.initialize();
                    run.initialized = true;
                }
            }
            finished = run.command.step();
            if (run.cancel) {
                cancelRun(run);
                return true;
            }
            if (finished) {
                run.command.finish(false);
                run.command.setContext(null);
                currentPosition++;
                if (currentPosition >= sequence.size()) {
                    logger.debug("Sequential command finished");
                    return true;
                }
                logger.debug("Sequential command advancing to command {}", currentPosition);
            }
        } while (finished);
        return false;
    }

    private void cancelRun(CommandRun run) {
        run.command.finish(true);
        run.command.setContext(null);
        getContext().cancelCommand(this);
    }

    @Override
    protected void finish(boolean interrupted) {
        logger.debug("finish {}", interrupted);
        if (sequence.size() == 0) return;
        if (interrupted && currentRun().command.isRunning()) {
            currentRun().command.setContext(null);
            currentRun().command.finish(true);
        }
    }

    @Override
    public void startCommand(Command command) {
        checkNotNull(command);
        if (getContext() == null)
            throw new CommandException("Cannot start a command in parent execution context if command group is not running");
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

    public Optional<CommandRun> findCommand(Command command) {
        return sequence.stream()
                .filter(run -> run.command == command)
                .findFirst();
    }
}
