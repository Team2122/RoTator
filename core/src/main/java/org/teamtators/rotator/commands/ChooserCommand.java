package org.teamtators.rotator.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.teamtators.rotator.CommandBase;
import org.teamtators.rotator.CoreRobot;
import org.teamtators.rotator.components.Chooser;
import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;

import java.util.Map;

public class ChooserCommand extends CommandBase implements Configurable<ChooserCommand.Config> {
    private final Map<String, Chooser<Command>> choosers;
    private final CommandStore commandStore;

    private Chooser<Command> chooser;
    private Command selectedCommand;
    private boolean started;

    public ChooserCommand(CoreRobot robot) {
        super("ChooserCommand");
        choosers = robot.commandChoosers();
        commandStore = robot.commandStore();
    }

    @Override
    public void configure(Config config) {
        chooser = choosers.get(config.chooser);
        if (chooser == null) {
            throw new ConfigException("Missing command chooser " + config.chooser);
        }
        for (String command : config.commands) {
            chooser.registerOption(command, commandStore.getCommand(command));
        }
        if (config.defaul != null) {
            chooser.registerDefault(config.defaul,
                    commandStore.getCommand(config.defaul));
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        started = false;
        selectedCommand = chooser.getSelected();
        logger.info("{} chosen command: {}", getName(), selectedCommand.getName());
        startWithContext(selectedCommand, this);
    }

    @Override
    protected boolean step() {
        boolean running = selectedCommand.isRunning();
        if (!running && started) return true;
        if (running && !started) started = true;
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {
        if (interrupted) {
            logger.warn("{} was interrupted. Interrupting chosen command {}", getName(), selectedCommand.getName());
            selectedCommand.cancel();
        } else {
            super.finish(false);
        }
    }

    static class Config {
        public String chooser;
        public String[] commands;
        @JsonProperty("default")
        public String defaul;
    }
}
