package org.teamtators.rotator.control;

import javax.inject.Inject;

public class InputDifferentiator extends AbstractSteppable implements ControllerInputProvider {
    private ControllerInputProvider inputProvider;
    private double lastInput;
    private double lastRate;

    @Inject
    public InputDifferentiator() {
        setExecutionOrder(50);
        reset();
    }

    public InputDifferentiator(ControllerInputProvider inputProvider) {
        this();
        setInputProvider(inputProvider);
    }

    public void reset() {
        lastInput = Double.NaN;
        lastRate = 0.0;
    }

    @Override
    public synchronized void step(double delta) {
        double input = inputProvider.getControllerInput();
        lastRate = Double.isNaN(lastInput) ? 0.0 : (input - lastInput) / delta;
        lastInput = input;
    }

    @Override
    public synchronized double getControllerInput() {
        return lastRate;
    }

    public void setInputProvider(ControllerInputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    public ControllerInputProvider getInputProvider() {
        return inputProvider;
    }
}
