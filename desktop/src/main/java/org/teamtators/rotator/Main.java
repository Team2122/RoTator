package org.teamtators.rotator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.commands.DriveTank;
import org.teamtators.rotator.commands.LogCommand;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurables;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.subsystems.SimulationDrive;
import org.teamtators.rotator.ui.SimulationFrame;
import org.teamtators.rotator.ui.WASDJoystick;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private final YAMLMapper yamlMapper = new YAMLMapper();
    private Scheduler scheduler = new Scheduler();
    private ConfigCommandStore commandStore = new ConfigCommandStore();
    private SimulationDrive drive = new SimulationDrive();
    private WASDJoystick driverJoystick = new WASDJoystick();
    private JFrame window;
    private List<Steppable> steppables = new ArrayList<>();
    private boolean running;

    public static void main(String[] args) {
        try {
            new Main().start();
        } catch (Exception e) {
            logger.error("Unhandled exception", e);
        }
    }

    public void start() {
        logger.info("Starting ui");

        ObjectNode commandsConfig = loadConfig(new File("./config/commands.yml"));
        ObjectNode simulationConfig = loadConfig(new File("./config/simulation.yml"));
        if (commandsConfig == null || simulationConfig == null) return;

        CommandBase.scheduler = scheduler;
        CommandBase.commandStore = commandStore;
        commandStore.registerClass(LogCommand.class);
        commandStore.registerClass(DriveTank.class);

        commandStore.createCommandsFromConfig(commandsConfig);

        Configurables.configureObject(drive, simulationConfig.get("SimulationDrive"), yamlMapper);
        CommandBase.drive = drive;

        CommandBase.driverJoystick = driverJoystick;

        steppables.add(drive);

        window = new SimulationFrame(drive, driverJoystick);
        window.setVisible(true);

        scheduler.startCommand(commandStore.getCommand("DriveTank"));

        running = true;
        Thread thread = new Thread(this::run);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping run loop");
            running = false;
            thread.interrupt();
        }));
        thread.start();
    }

    private ObjectNode loadConfig(File file) {
        ObjectNode commandsConfig;
        try (InputStream fileStream = new FileInputStream(file)) {
            commandsConfig = (ObjectNode) yamlMapper.reader().readTree(fileStream);
            logger.info("Loaded commands config");
        } catch (IOException | ConfigException e) {
            logger.error("Error loading commands config", e);
            return null;
        }
        return commandsConfig;
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
