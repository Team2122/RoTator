package org.teamtators.rotator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.Scheduler;

public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Robot main");
        Scheduler scheduler = new Scheduler();
//        scheduler.startCommand(command);
//        scheduler.startCommand(new OneShotCommand("OneShotTest", () -> {
//            logger.info("Hello world!");
//        }));

        Command command1 = new TestCommand();
        Command command2 = Command.log("hello 2");
        Command seq = Command.sequence("TestGroup", command1, command2);
//        Command command = new TestCommand();
        scheduler.startCommand(command1);
        scheduler.startCommand(command2);
        scheduler.startCommand(seq);
        while (true) {
//            scheduler.startCommand(command1);
//            scheduler.startCommand(command2);
            if (!seq.isRunning())
                scheduler.startCommand(seq);
            scheduler.execute();
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                logger.info("Interrupted");
                break;
            }
        }
    }

    private static class TestCommand extends Command {
        int count;
        public TestCommand() {
            super("TestCommand");
        }

        @Override
        protected void initialize() {
            logger.info("initialize");
            count = 0;
        }

        @Override
        protected boolean step() {
            logger.info("step count: {}", count);
            return count++ >= 3;
        }

        @Override
        protected void finish(boolean interrupted) {
            logger.info("finish {}", interrupted);
        }
    }

}
