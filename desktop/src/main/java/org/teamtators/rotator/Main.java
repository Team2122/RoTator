package org.teamtators.rotator;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.commands.DriveTank;
import org.teamtators.rotator.commands.LogCommand;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.subsystems.SimulationDrive;
import org.teamtators.rotator.ui.SimulationFrame;
import org.teamtators.rotator.ui.WASDJoystick;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);
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
        logger.info("Starting simulation");

        YAMLMapper yamlMapper = new YAMLMapper();
        ObjectNode commandsConfig;
        try (InputStream file = new FileInputStream("./config/commands.yml")) {
            commandsConfig = (ObjectNode) yamlMapper.reader().readTree(file);
            logger.info("Loaded commands config");
        } catch (IOException | ConfigException e) {
            logger.error("Error loading commands config", e);
            return;
        }

        CommandBase.scheduler = scheduler;
        CommandBase.commandStore = commandStore;
        commandStore.registerClass(LogCommand.class);
        commandStore.registerClass(DriveTank.class);

        commandStore.createCommandsFromConfig(commandsConfig);

        SimulationDrive.Config config = new SimulationDrive.Config();
        config.powerToVelocity = 100.0;
        config.wheelWidth = 24.0;
        drive.configure(config);
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

    public void run() {
        int delay = 20;
        while (running) {
            scheduler.execute();
            for (Steppable steppable : steppables) {
                steppable.step(delay / 1000.0);
            }
            window.repaint();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
