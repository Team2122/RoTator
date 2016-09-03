package org.teamtators.rotator.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractController implements Steppable {
    private Logger logger;
    private String name;
    private ControllerInputProvider inputProvider;
    private ControllerOutputConsumer outputConsumer;
    private ITargetChecker targetChecker;
    private double setpoint;
    private double input;
    private double output;
    private boolean onTarget;
    private boolean disableOnTarget;

    private Stepper stepper;

    public AbstractController(String name) {
        logger = LoggerFactory.getLogger(this.getClass());
        this.name = name;
        reset();
    }

    public void reset() {
        setSetpoint(0);
        input = 0.0;
        output = 0.0;
    }

    @Override
    public void step(double delta) {
        checkNotNull(inputProvider, "input must be set on a controller before using");
        checkNotNull(outputConsumer, "output must be set on a controller before using");
        checkNotNull(targetChecker, "targetChecker must be set on a controller before using");
        input = this.inputProvider.getControllerInput();
        output = computeOutput(delta);
        outputConsumer.setControllerOutput(output);
        onTarget = targetChecker.compute(delta, this);
        if(onTarget && disableOnTarget) {
            disable();
        }
    }

    protected abstract double computeOutput(double delta);

    public ControllerInputProvider getInputProvider() {
        synchronized (this) {
            return inputProvider;
        }
    }

    public AbstractController setInputProvider(ControllerInputProvider inputProvider) {
        this.inputProvider = inputProvider;
        return this;
    }

    public ControllerOutputConsumer getOutputConsumer() {
        synchronized (this) {
            return outputConsumer;
        }
    }

    public AbstractController setOutputConsumer(ControllerOutputConsumer outputConsumer) {
        this.outputConsumer = outputConsumer;
        return this;
    }

    public ITargetChecker getTargetChecker() {
        synchronized (this) {
            return targetChecker;
        }
    }

    public void setTargetChecker(ITargetChecker targetChecker) {
        this.targetChecker = targetChecker;
    }

    public double getSetpoint() {
        synchronized (this) {
            return setpoint;
        }
    }

    public AbstractController setSetpoint(double setpoint) {
        this.setpoint = setpoint;
        return this;
    }

    protected double getInput() {
        synchronized (this) {
            return input;
        }
    }

    protected double getError() {
        return getSetpoint() - getInput();
    }

    public String getName() {
        synchronized (this) {
            return name;
        }
    }

    public boolean isOnTarget() {
        synchronized (this) {
            return onTarget;
        }
    }

    @Inject
    public void setStepper(Stepper stepper) {
        this.stepper = stepper;
    }

    public void enable() {
        stepper.add(this);
    }

    public void disable() {
        stepper.remove(this);
    }

    public boolean isEnabled() {
        return stepper.isEnabled(this);
    }
}
