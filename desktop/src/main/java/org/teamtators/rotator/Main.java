package org.teamtators.rotator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigLoader;
import org.teamtators.rotator.config.Configurables;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.subsystems.SimulationDrive;
import org.teamtators.rotator.ui.SimulationFrame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import org.teamtators.rotator.scheduler.*;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private final YAMLMapper yamlMapper = new YAMLMapper();
    private JFrame window;
    private List<Steppable> steppables = new ArrayList<>();
    private boolean running;
    private Scheduler scheduler;

    public static void main(String[] args) {
        try {
            new Main().start();
        } catch (Exception e) {
            logger.error("Unhandled exception", e);
        }
    }

    public void start() {
        logger.info("Starting ui");

        Injector injector = Guice.createInjector(new DesktopModule());

        ConfigLoader configLoader = injector.getInstance(ConfigLoader.class);
        ConfigCommandStore commandStore = injector.getInstance(ConfigCommandStore.class);
        SimulationDrive drive = injector.getInstance(SimulationDrive.class);
        scheduler = injector.getInstance(Scheduler.class);

        ObjectNode commandsConfig = (ObjectNode) configLoader.load("commands.yml");
        ObjectNode simulationConfig = (ObjectNode) configLoader.load("simulation.yml");

        commandStore.createCommandsFromConfig(commandsConfig);

        Configurables.configureObject(drive, simulationConfig.get("SimulationDrive"), yamlMapper);

        steppables.add(drive);

        window = injector.getInstance(SimulationFrame.class);
        window.setVisible(true);

        scheduler.startCommand(commandStore.getCommand("DriveTank"));

        running = true;
        Thread thread = new Thread(this::run);
        thread.setUncaughtExceptionHandler((t, e) ->
                logger.error("Uncaught exception in thread {}", t, e)
        );
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping run loop");
            running = false;
            thread.interrupt();
        }));
        thread.start();
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
            window.repaint();
            long nanoTime = System.nanoTime();
            long elapsed = (nanoTime - lastRun);
            int elapsedMS = (int) (elapsed / 1000000);
            logger.trace("Elapsed {} ms, max {} ms", elapsedMS, periodMS);
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
