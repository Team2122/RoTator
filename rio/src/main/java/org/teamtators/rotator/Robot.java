package org.teamtators.rotator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigLoader;
import org.teamtators.rotator.config.Configurables;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.subsystems.AbstractDrive;

import javax.inject.Inject;
import java.util.List;

/**
 * The main robot class for RoTator
 */
public class Robot extends IterativeRobot {
    private static final Logger logger = LoggerFactory.getLogger(Robot.class);
    @Inject
    private ConfigLoader configLoader;
    @Inject
    private ConfigCommandStore commandStore;
    @Inject
    private AbstractOperatorInterface operatorInterface;
    @Inject
    private List<Subsystem> subsystems;
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private Scheduler scheduler;

    @Override
    public void robotInit() {
        try {
            initialize();
        } catch (Throwable t) {
            logger.error("Exception during robot initialization", t);
        }
    }

    private void initialize() {
        logger.info("Robot is initializing");

        Injector coreInjector = Guice.createInjector(new CoreModule()
                .withConfigDir("/home/lvuser/config"));
        configLoader = coreInjector.getInstance(ConfigLoader.class);

        logger.debug("Created injector. Loading configs");
        ObjectNode commandsConfig = (ObjectNode) configLoader.load("commands.yml");
        ObjectNode subsystemsConfig = (ObjectNode) configLoader.load("subsystems.yml");

        Injector injector = coreInjector.createChildInjector(new RioModule());
        injector.injectMembers(this);

        commandStore.setInjector(injector);

        logger.debug("Creating commands");
        commandStore.createCommandsFromConfig(commandsConfig);

        logger.debug("Configuring subsystems");
        for (Subsystem subsystem : subsystems) {
            String name = subsystem.getName();
            JsonNode config = subsystemsConfig.get(name);
            if (config != null)
                Configurables.configureObject(subsystem, config, objectMapper);
            scheduler.registerSubsystem(subsystem);
        }

        logger.debug("Configuring triggers");
        scheduler.onTrigger(operatorInterface.driverJoystick().getTriggerSource(LogitechF310.Button.A))
                .start(Command.log("Button A pressed"))
                .whenPressed()
                .start(Command.log("Button A released"))
                .whenReleased();

        logger.info("Robot initialized");
    }

    @Override
    public void disabledInit() {
        logger.info("Robot disabled");
    }

    @Override
    public void autonomousInit() {
        logger.info("Robot enabled in autonomous");
    }

    @Override
    public void teleopInit() {
        logger.info("Robot enabled in teleop");
        scheduler.startCommand(commandStore.getCommand("DriveTank"));
    }

    @Override
    public void testInit() {
        logger.info("Robot enabled in test");
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousPeriodic() {
        scheduler.execute();
    }

    @Override
    public void teleopPeriodic() {
        scheduler.execute();
    }

    @Override
    public void testPeriodic() {
    }
}
