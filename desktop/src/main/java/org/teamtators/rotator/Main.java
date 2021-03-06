package org.teamtators.rotator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigLoader;
import org.teamtators.rotator.config.Configurables;
import org.teamtators.rotator.control.Steppable;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.scheduler.*;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.ManualTester;
import org.teamtators.rotator.ui.SimulationFrame;

public class Main implements StateListener {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private Stepper stepper;
    private Stepper uiStepper;
    private DesktopRobot robot;
    private Command autoCommand;
    private Scheduler scheduler;

    public static void main(String[] args) {
        try {
            new Main().start();
        } catch (Exception e) {
            logger.error("Unhandled exception", e);
            System.exit(1);
        }
    }

    public void start() {
        logger.info("Starting simulation robot");

        robot = DaggerDesktopRobot.create();
        stepper = robot.stepper();
        uiStepper = robot.uiStepper();
        ConfigLoader configLoader = robot.configLoader();
        scheduler = robot.scheduler();
        ManualTester manualTester = robot.manualTester();
        ConfigCommandStore commandStore = robot.commandStore();
        SimulationFrame simulationFrame = robot.simulationFrame();

        commandStore.setRobot(robot);

        logger.debug("Loading configs");
        ObjectNode commandsConfig = (ObjectNode) configLoader.load("commands.yml");
        ObjectNode simulationConfig = (ObjectNode) configLoader.load("simulation.yml");
        ObjectNode triggersConfig = (ObjectNode) configLoader.load("triggers.yml");

        logger.debug("Configuring subsystems");
        for (Subsystem subsystem : robot.subsystems()) {
            String name = subsystem.getName();
            JsonNode config = simulationConfig.get(name);
            Configurables.configureObject(subsystem, config, robot.objectMapper());
            if (subsystem instanceof StateListener) {
                scheduler.registerStateListener((StateListener) subsystem);
            }
            if (subsystem instanceof ITestable) {
                logger.trace("Registering test group for subsystem {}", subsystem.getName());
                manualTester.registerTestGroup(((ITestable) subsystem).getTestGroup());
            }
            if (subsystem instanceof Steppable) {
                stepper.add((Steppable) subsystem);
            }
        }

        logger.debug("Creating commands");
        commandStore.createCommandsFromConfig(commandsConfig);

        logger.debug("Configuring triggers");
        robot.triggerBinder().bindTriggers(triggersConfig);

        uiStepper.setPeriod(1.0 / 50.0);
        uiStepper.add(delta -> {
            scheduler.execute();
            simulationFrame.repaint();
        });

        logger.info("Opening window");
        simulationFrame.setVisible(true);

        autoCommand = commandStore.getCommand("AutoChooser");
        scheduler.registerStateListener(this);

        scheduler.enterState(RobotState.DISABLED);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        logger.debug("Starting steppers");
        stepper.start();
        uiStepper.start();
    }

    private void stop() {
        logger.info("Stopping steppers");
        stepper.stop();
        uiStepper.stop();
    }

    @Override
    public void onEnterState(RobotState newState) {
        if(newState == RobotState.AUTONOMOUS) {
            scheduler.startCommand(autoCommand);
        }
    }
}
