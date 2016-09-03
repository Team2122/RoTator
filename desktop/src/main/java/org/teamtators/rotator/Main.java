package org.teamtators.rotator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigLoader;
import org.teamtators.rotator.config.Configurables;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Commands;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.subsystems.SimulationDrive;
import org.teamtators.rotator.ui.SimulationDisplay;
import org.teamtators.rotator.ui.SimulationFrame;
import org.teamtators.rotator.ui.WASDJoystick;

import javax.inject.Inject;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private SimulationDisplay simulationDisplay;
    @Inject
    private SimulationFrame simulationFrame;
    @Inject
    private Scheduler scheduler;
    @Inject
    private ConfigLoader configLoader;
    @Inject
    private ConfigCommandStore commandStore;
    @Inject
    private SimulationDrive drive;
    @Inject
    private WASDJoystick joystick;
    @Inject
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

        logger.debug("Configuring subsystems");
        Configurables.configureObject(drive, simulationConfig.get("SimulationDrive"), objectMapper);

        logger.debug("Creating commands from config");
        commandStore.createCommandsFromConfig(commandsConfig);

        logger.debug("Binding triggers");
        scheduler.onTrigger(joystick.getTriggerSource(LogitechF310.Button.A))
                .start(Commands.log("Button A pressed"))
                .whenPressed()
                .start(Commands.log("Button A released"))
                .whenReleased();

        stepper.add(drive);
        uiStepper.setPeriod(1000 / 50);
        uiStepper.add(delta -> {
            scheduler.execute();
            simulationFrame.repaint();
        });

        logger.info("Opening window");
        simulationFrame.setVisible(true);

        scheduler.enterState(RobotState.DISABLED);
        scheduler.registerDefaultCommand(commandStore.getCommand("DriveTank"));

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
