package org.teamtators.rotator;

import dagger.Component;
import org.teamtators.rotator.control.Stepper;
import org.teamtators.rotator.ui.SimulationFrame;

import javax.inject.Singleton;

@Component(modules = DesktopModule.class)
@Singleton
public interface DesktopRobot extends CoreRobot {
    SimulationFrame simulationFrame();

    Stepper uiStepper();
}
