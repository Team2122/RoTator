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
import org.teamtators.rotator.config.TriggerBinder;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.scheduler.RobotState;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.tester.ITestable;
import org.teamtators.rotator.tester.ManualTester;

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
    @Inject
    private ManualTester manualTester;
    @Inject
    private TriggerBinder triggerBinder;
    @Inject
    private Stepper stepper;

    @Override
    public void robotInit() {
        try {
            initialize();
        } catch (Throwable t) {
            logger.error("Exception during robot initialization", t);
//            System.exit(1);
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
        ObjectNode triggersConfig = (ObjectNode) configLoader.load("triggers.yml");

        Injector injector = coreInjector.createChildInjector(new RioModule());
        injector.injectMembers(this);

        commandStore.setInjector(injector);

        logger.debug("Configuring subsystems");
        for (Subsystem subsystem : subsystems) {
            String name = subsystem.getName();
            JsonNode config = subsystemsConfig.get(name);
            Configurables.configureObject(subsystem, config, objectMapper);
            scheduler.registerSubsystem(subsystem);
            if (subsystem instanceof ITestable) {
                logger.trace("Registering test group for subsystem {}", subsystem.getName());
                manualTester.registerTestGroup(((ITestable) subsystem).getTestGroup());
            }
        }
        manualTester.setJoystick(operatorInterface.driverJoystick());

        logger.debug("Creating commands");
        commandStore.createCommandsFromConfig(commandsConfig);

        logger.debug("Configuring triggers");
        triggerBinder.setCommandStore(commandStore);
        triggerBinder.bindTriggers(triggersConfig);

        scheduler.registerDefaultCommand(commandStore.getCommand("DriveTank"));
        scheduler.registerDefaultCommand(manualTester);

        logger.debug("Starting stepper");
        stepper.start();

        logger.info("Robot initialized");
    }

    @Override
    public void disabledInit() {
        scheduler.enterState(RobotState.DISABLED);
    }

    @Override
    public void autonomousInit() {
        scheduler.enterState(RobotState.AUTONOMOUS);
    }

    @Override
    public void teleopInit() {
        scheduler.enterState(RobotState.TELEOP);
    }

    @Override
    public void testInit() {
        scheduler.enterState(RobotState.TEST);
    }

    @Override
    public void disabledPeriodic() {
        scheduler.execute();
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
        scheduler.execute();
    }
}
