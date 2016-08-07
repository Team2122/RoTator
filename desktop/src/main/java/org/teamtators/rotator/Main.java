package org.teamtators.rotator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.teamtators.rotator.commands.LogCommand;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.Scheduler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Robot main");
        Scheduler scheduler = new Scheduler();
        ConfigCommandStore commandStore = new ConfigCommandStore();
        commandStore.registerClass(TestCommand.class);
        commandStore.registerClass(LogCommand.class);

        YAMLMapper yamlMapper = new YAMLMapper();
        ObjectNode commandsConfig;
        try (InputStream file = new FileInputStream("./config/commands.yml")) {
            commandsConfig = (ObjectNode) yamlMapper.reader().readTree(file);
            commandStore.createCommandsFromConfig(commandsConfig);
            logger.info("Loaded commands config");
        } catch (IOException | ConfigException e) {
            logger.error("Error loading commands config", e);
            return;
        }

        Command seq = commandStore.getCommand("$Sequence");
        scheduler.startCommand(seq);
        while (true) {
            scheduler.execute();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                logger.info("Interrupted");
                break;
            }
        }
    }

    public static class TestCommand extends Command implements Configurable<TestCommand.Config> {
        public static class Config {
            public int maxCount;
        }

        private Config config;

        @Override
        public void configure(Config config) {
            this.config = config;
            logger.debug("configured TestCommand, maxCount: " + config.maxCount);
        }

        int count;
        public TestCommand() {
            super("TestCommand");
        }

        @Override
        protected void initialize() {
            if (config == null) throw new NullPointerException("config is null");
            logger.info("initialize");
            count = 0;
        }

        @Override
        protected boolean step() {
            logger.info("step count: {}", count);
            return count++ >= config.maxCount;
        }

        @Override
        protected void finish(boolean interrupted) {
            logger.info("finish {}", interrupted);
        }
    }

}
