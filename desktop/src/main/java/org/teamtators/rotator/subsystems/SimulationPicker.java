package org.teamtators.rotator.subsystems;

import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.control.Steppable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SimulationPicker extends AbstractPicker implements Steppable, Configurable<SimulationPicker.Config> {
    private SimulationMotor pickerMotor = new SimulationMotor();
    private SimulationEncoder pickerEncoder = new SimulationEncoder();

    @Inject
    public SimulationPicker() {
        pickerEncoder.setMotor(pickerMotor);
    }

    @Override
    public int getExecutionOrder() {
        return 200;
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
    public boolean isAtCheval() {
        return false;
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
