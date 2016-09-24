package org.teamtators.rotator.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.Configurable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParallelCommand extends Command implements CommandRunContext, Configurable<ParallelCommand.Config> {
    private List<CommandRun> running = new ArrayList<>();
    private ConfigCommandStore commandStore;

    public ParallelCommand(Command... commands) {
        this();
        for(int i = 0; i < commands.length; i++) {
            running.add(new CommandRun(commands[i]));
        }
    }

    public ParallelCommand(CoreRobot robot) {
        this();
        this.commandStore = robot.commandStore();

    }

    public ParallelCommand(){
        super("ParallelCommand");
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
        getContext().startCommand(command);
    }

    @Override
    public void configure(Config config) {
        for(JsonNode node : config.commands) {
            running.add(new CommandRun(commandStore.getCommandForSubcontext(getName(), node)));
        }
    }

    static class Config {
        JsonNode commands;
    }
}
