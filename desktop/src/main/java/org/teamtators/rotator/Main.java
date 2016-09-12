package org.teamtators.rotator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigLoader;
import org.teamtators.rotator.config.Configurables;
import org.teamtators.rotator.config.TriggerBinder;
import org.teamtators.rotator.control.ForController;
import org.teamtators.rotator.control.Steppable;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.*;
import org.teamtators.rotator.subsystems.SimulationDrive;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.ManualTester;
import org.teamtators.rotator.ui.SimulationDisplay;
import org.teamtators.rotator.ui.SimulationFrame;
import org.teamtators.rotator.ui.WASDJoystick;

import javax.inject.Inject;
import java.util.List;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private SimulationFrame simulationFrame;
    @Inject
    private AbstractOperatorInterface operatorInterface;
    @Inject
    private Scheduler scheduler;
    @Inject
    private ManualTester manualTester;
    @Inject
    private ConfigLoader configLoader;
    @Inject
    private ConfigCommandStore commandStore;
    @Inject
    private List<Subsystem> subsystems;
    @Inject
    private TriggerBinder triggerBinder;
    @Inject @ForController
    private Stepper stepper;
    @Inject
    private Stepper uiStepper;

    public static void main(String[] args) {
        try {
            new Main().start();
        } catch (Exception e) {
            logger.error("Unhandled exception", e);
        }
    }

    public void start() {
        logger.info("Starting simulation robot");

        Injector injector = Guice.createInjector(new DesktopModule());
        injector.injectMembers(this);

        logger.debug("Loading configs");
        ObjectNode commandsConfig = (ObjectNode) configLoader.load("commands.yml");
        ObjectNode simulationConfig = (ObjectNode) configLoader.load("simulation.yml");
        ObjectNode triggersConfig = (ObjectNode) configLoader.load("triggers.yml");

        logger.debug("Configuring subsystems");
        for (Subsystem subsystem : subsystems) {
            String name = subsystem.getName();
            JsonNode config = simulationConfig.get(name);
            Configurables.configureObject(subsystem, config, objectMapper);
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
        triggerBinder.bindTriggers(triggersConfig);
        scheduler.registerDefaultCommand(commandStore.getCommand("DriveTank"));

        uiStepper.setPeriod(1.0 / 50.0);
        uiStepper.add(delta -> {
            scheduler.execute();
            simulationFrame.repaint();
        });

        logger.info("Opening window");
        simulationFrame.setVisible(true);

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
}
