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
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Commands;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.subsystems.SimulationDrive;
import org.teamtators.rotator.ui.SimulationFrame;
import org.teamtators.rotator.ui.WASDJoystick;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    @Inject
    private ObjectMapper objectMapper;
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

    private List<Steppable> steppables = new ArrayList<>();
    private boolean running;
    private Thread thread;

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

        steppables.add(drive);

        logger.info("Opening window");
        simulationFrame.setVisible(true);

        scheduler.enterState(RobotState.TELEOP);
        scheduler.registerDefaultCommand(commandStore.getCommand("DriveTank"));

        running = true;
        thread = new Thread(this::run);
        thread.setUncaughtExceptionHandler((t, e) -> {
                    logger.error("Uncaught exception in thread {}", t, e);
                    System.exit(-1);
                }
        );
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        thread.start();
    }

    private void stop() {
        logger.info("Stopping run loop");
        running = false;
        thread.interrupt();
    }

    public void run() {
        int periodMS = 20;
        double periodS = periodMS / 1000.0;
        long lastRun;
        while (running) {
            lastRun = System.nanoTime();
            scheduler.execute();
            for (Steppable steppable : steppables) {
                steppable.step(periodS);
            }
            simulationFrame.repaint();
            long nanoTime = System.nanoTime();
            long elapsed = (nanoTime - lastRun);
            int elapsedMS = (int) (elapsed / 1000000);
//            logger.trace("Elapsed {} ms, max {} ms", elapsedMS, periodMS);
            if (elapsedMS >= periodMS) {
                logger.debug("{} ms elapsed, greater than period of {} ms", elapsedMS, periodMS);
                continue;
            }
            try {
                Thread.sleep(periodMS - elapsedMS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
