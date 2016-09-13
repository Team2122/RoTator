package org.teamtators.rotator.subsystems;

import com.google.inject.Singleton;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Steppable;

@Singleton
public class SimulationPicker extends AbstractPicker implements Steppable, Configurable<SimulationPicker.Config> {
    private SimulationMotor pickerMotor = new SimulationMotor();
    private SimulationEncoder pickerEncoder = new SimulationEncoder();

    public SimulationPicker() {
        pickerEncoder.setMotor(pickerMotor);
    }

    @Override
    public void step(double delta) {
        pickerMotor.step(delta);
    }

    @Override
    public void setPower(double power) {
        pickerMotor.setPower(power);
    }

    public double getPickerRate() {
        return pickerEncoder.getRate();
    }

    public double getPickerRotations() {
        return pickerEncoder.getDistance();
    }

    @Override
    public void configure(Config config) {
        pickerMotor.configure(config.motor);
        pickerEncoder.configure(config.encoder);
    }

    public static class Config {
        public SimulationMotor.Config motor;
        public SimulationEncoder.Config encoder;
    }
}
