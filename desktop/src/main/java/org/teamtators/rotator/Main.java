package org.teamtators.rotator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.commands.LogCommand;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.Subsystem;

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
        TestSubsystem testSubsystem = new TestSubsystem();
        TestCommand.testSubsystem = testSubsystem;

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

//        Command command1 = commandStore.getCommand("TestCommand");
//        Command command2 = commandStore.getCommand("TestCommand2");
//        scheduler.startCommand(command1);
//        scheduler.startCommand(command2);
        scheduler.startCommand(commandStore.getCommand("$Seq2"));
        while (true) {
            scheduler.startCommand(commandStore.getCommand("TestCommand2"));
            scheduler.execute();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                logger.info("Interrupted");
                break;
            }
        }
    }

    public static class TestSubsystem extends Subsystem {
        public TestSubsystem() {
            super("TestSubsystem");
        }
    }

    public static class TestCommand extends CommandBase implements Configurable<TestCommand.Config> {
        int count;
        private Config config;

        public static TestSubsystem testSubsystem;

        public TestCommand() {
            super("TestCommand");
            requires(testSubsystem);
        }

        @Override
        public void configure(Config config) {
            this.config = config;
            logger.debug("configured TestCommand, maxCount: " + config.maxCount);
        }

        @Override
        protected void initialize() {
            super.initialize();
            count = 0;
        }

        @Override
        protected boolean step() {
            logger.info("step count: {}", count);
            return count++ >= config.maxCount;
        }

        @Override
        protected void finish(boolean interrupted) {
            super.finish(interrupted);
        }

        public static class Config {
            public int maxCount;
        }
    }

}
