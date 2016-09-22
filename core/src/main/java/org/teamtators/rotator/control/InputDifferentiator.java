package org.teamtators.rotator.control;

import javax.inject.Inject;

public class InputDifferentiator extends AbstractSteppable implements ControllerInputProvider {
    private ControllerInputProvider inputProvider;
    private double lastInput;
    private double lastRate;

    @Inject
    public InputDifferentiator() {
        reset();
    }

    public InputDifferentiator(ControllerInputProvider inputProvider) {
        this();
        setInputProvider(inputProvider);
    }

    public void reset() {
        lastInput = 0.0;
        lastRate = 0.0;
    }

    @Override
    public void step(double delta) {
        double input = inputProvider.getControllerInput();
        lastRate = (input - lastInput) / delta;
        lastInput = input;
    }

    @Override
    public double getControllerInput() {
        return lastRate;
    }

    public void setInputProvider(ControllerInputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    public ControllerInputProvider getInputProvider() {
        return inputProvider;
    }
}
