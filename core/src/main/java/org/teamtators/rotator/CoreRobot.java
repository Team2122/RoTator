package org.teamtators.rotator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.teamtators.rotator.commands.IChooser;
import org.teamtators.rotator.config.ConfigCommandStore;
import org.teamtators.rotator.config.ConfigLoader;
import org.teamtators.rotator.config.ControllerFactory;
import org.teamtators.rotator.config.TriggerBinder;
import org.teamtators.rotator.control.ForController;
import org.teamtators.rotator.control.ITimeProvider;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.Subsystem;
import org.teamtators.rotator.subsystems.AbstractDrive;
import org.teamtators.rotator.subsystems.AbstractPicker;
import org.teamtators.rotator.subsystems.AbstractTurret;
import org.teamtators.rotator.subsystems.AbstractVision;
import org.teamtators.rotator.tester.ManualTester;

import javax.inject.Named;
import java.util.List;

public interface CoreRobot {
    ObjectMapper objectMapper();

    AbstractDrive drive();

    AbstractPicker picker();

    AbstractTurret turret();

    AbstractVision vision();

    AbstractOperatorInterface operatorInterface();

    ITimeProvider timeProvider();

    Scheduler scheduler();

    ConfigCommandStore commandStore();

    ConfigLoader configLoader();

    ControllerFactory controllerFactory();

    List<Subsystem> subsystems();

    TriggerBinder triggerBinder();

    ManualTester manualTester();

    @ForController
    Stepper stepper();

    @Named("autoChooser")
    IChooser<Command> autoChooser();

    DataCollector dataCollector();
}
