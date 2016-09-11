package org.teamtators.rotator.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractController implements Steppable {
    protected Logger logger;
    private String name;

    private ControllerInputProvider inputProvider;
    private ControllerOutputConsumer outputConsumer;
    private OnTargetChecker targetChecker = OnTargetCheckers.neverOnTarget();
    private OnTargetHandler onTargetHandler = null;

    private double setpoint;
    private double input;
    private double output;
    private boolean onTarget;

    private Stepper stepper;

    public AbstractController() {
        this("");
    }

    public AbstractController(String name) {
        reset();
        setName(name);
    }

    public synchronized void reset() {
        disable();
        setpoint = 0.0;
        input = 0.0;
        output = 0.0;
    }


    @Override
    public void step(double delta) {
        checkNotNull(inputProvider, "input must be set on a controller before using");
        checkNotNull(outputConsumer, "output must be set on a controller before using");

        onTarget = targetChecker.compute(delta, this);
        if (onTarget && onTargetHandler != null) {
            onTargetHandler.onTarget(this);
        }

        input = this.inputProvider.getControllerInput();
        output = computeOutput(delta);
        outputConsumer.setControllerOutput(output);
    }

    protected abstract double computeOutput(double delta);

    public ControllerInputProvider getInputProvider() {
        return inputProvider;
    }

    public synchronized void setInputProvider(ControllerInputProvider inputProvider) {
        checkNotNull(inputProvider);
        this.inputProvider = inputProvider;
    }

    public synchronized ControllerOutputConsumer getOutputConsumer() {
        return outputConsumer;
    }

    public synchronized void setOutputConsumer(ControllerOutputConsumer outputConsumer) {
        checkNotNull(outputConsumer);
        this.outputConsumer = outputConsumer;
    }

    public synchronized OnTargetChecker getTargetChecker() {
        return targetChecker;
    }

    public synchronized void setTargetChecker(OnTargetChecker targetChecker) {
        checkNotNull(targetChecker);
        this.targetChecker = targetChecker;
    }

    public synchronized double getSetpoint() {
        return setpoint;
    }

    public synchronized void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    protected synchronized double getInput() {
        return input;
    }

    protected synchronized double getError() {
        return setpoint - input;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        String loggerName = String.format("%s(%s)", this.getClass().getName(), name);
        logger = LoggerFactory.getLogger(loggerName);
    }

    public synchronized boolean isOnTarget() {
        return onTarget;
    }

    @Inject
    public void setStepper(@ForController Stepper stepper) {
        this.stepper = stepper;
    }

    public void enable() {
        checkNotNull(stepper, "A controller must be assigned a stepper before being enabled");
        stepper.add(this);
    }

    public void disable() {
        if (outputConsumer != null)
            outputConsumer.setControllerOutput(0.0);
        if (stepper != null)
            stepper.remove(this);
    }

    public boolean isEnabled() {
        return stepper != null && stepper.contains(this);
    }

    public synchronized OnTargetHandler getOnTargetHandler() {
        return onTargetHandler;
    }

    public synchronized void setOnTargetHandler(OnTargetHandler onTargetHandler) {
        this.onTargetHandler = onTargetHandler;
    }
}
