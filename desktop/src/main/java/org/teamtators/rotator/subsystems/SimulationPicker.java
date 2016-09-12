package org.teamtators.rotator.subsystems;

import com.google.inject.Singleton;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Steppable;

@Singleton
public class SimulationPicker extends AbstractPicker implements Steppable, Configurable<SimulationPicker.Config> {
    private SimulationMotor pickerMotor = new SimulationMotor();

    public SimulationPicker() {
    }

    @Override
    public void step(double delta) {
        pickerMotor.step(delta);
    }

    @Override
    public void setPower(double power) {
        pickerMotor.setPower(power);
    }

    @Override
    public void configure(Config config) {
        pickerMotor.configure(config.motor);
    }

    public static class Config {
        public SimulationMotor.Config motor;
    }
}
