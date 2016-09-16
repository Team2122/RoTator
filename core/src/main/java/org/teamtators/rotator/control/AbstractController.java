package org.teamtators.rotator.control;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.config.ConfigException;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractController extends AbstractSteppable {
    protected Logger logger;
    private String name;

    private ControllerInputProvider inputProvider;
    private ControllerOutputConsumer outputConsumer;
    private ControllerPredicate targetPredicate = ControllerPredicates.alwaysFalse();
    private LimitPredicate limitPredicate = LimitPredicates.nevetAtLimit();
    private OnTargetHandler onTargetHandler = null;

    private double minSetpoint = Double.NEGATIVE_INFINITY;
    private double maxSetpoint = Double.POSITIVE_INFINITY;
    private double minOutput = Double.NEGATIVE_INFINITY;
    private double maxOutput = Double.POSITIVE_INFINITY;

    private volatile double setpoint;
    private volatile double input;
    private volatile double output;
    private volatile boolean onTarget;
    private volatile LimitState limitState;

    public AbstractController() {
        this("");
    }

    public AbstractController(String name) {
        reset();
        setName(name);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value > max) return max;
        else if (value < min) return min;
        else return value;
    }

    public synchronized void reset() {
        disable();
        setpoint = 0.0;
        input = 0.0;
        output = 0.0;
        onTarget = false;
        limitState = LimitState.NEITHER;
    }


    @Override
    public final void step(double delta) {
        synchronized (this) {
            input = this.inputProvider.getControllerInput();
            onTarget = targetPredicate.compute(delta, this);
            limitState = limitPredicate.getLimit(this);
        }

        if (onTarget && onTargetHandler != null) {
            onTargetHandler.onTarget(this);
        }

        double computedOutput = computeOutput(delta);
        double minOutput = this.minOutput;
        double maxOutput = this.maxOutput;
        if (limitState == LimitState.POSITIVE) {
            maxOutput = 0;
        } else if (limitState == LimitState.NEGATIVE) {
            minOutput = 0;
        }
        computedOutput = applyLimits(computedOutput, minOutput, maxOutput);

        synchronized (this) {
            output = computedOutput;
            outputConsumer.setControllerOutput(output);
        }
    }

    protected abstract double computeOutput(double delta);

    public ControllerInputProvider getInputProvider() {
        return inputProvider;
    }

    public void setInputProvider(ControllerInputProvider inputProvider) {
        checkNotNull(inputProvider);
        this.inputProvider = inputProvider;
    }

    public ControllerOutputConsumer getOutputConsumer() {
        return outputConsumer;
    }

    public void setOutputConsumer(ControllerOutputConsumer outputConsumer) {
        checkNotNull(outputConsumer);
        this.outputConsumer = outputConsumer;
    }

    public ControllerPredicate getTargetPredicate() {
        return targetPredicate;
    }

    public void setTargetPredicate(ControllerPredicate targetPredicate) {
        checkNotNull(targetPredicate);
        this.targetPredicate = targetPredicate;
    }

    public void configureTarget(JsonNode config) {
        if (config == null) return;
        if (!config.isObject()) {
            throw new ConfigException("Controller target config must be an object");
        }
        ControllerPredicate targetPredicate;
        if (config.has("within")) {
            targetPredicate = ControllerPredicates.withinError(config.get("within").asDouble());
        } else {
            targetPredicate = getTargetPredicate();
        }
        if (config.has("time")) {
            double time = config.get("time").asDouble();
            targetPredicate = new ControllerPredicates.SampleTime(time, targetPredicate);
        }
        setTargetPredicate(targetPredicate);
        if (config.has("disable") && config.get("disable").asBoolean()) {
            setOnTargetHandler(OnTargetHandlers.disableController());
        }
    }

    public LimitPredicate getLimitPredicate() {
        return limitPredicate;
    }

    public void setLimitPredicate(LimitPredicate limitPredicate) {
        checkNotNull(limitPredicate);
        this.limitPredicate = limitPredicate;
    }

    public OnTargetHandler getOnTargetHandler() {
        return onTargetHandler;
    }

    public void setOnTargetHandler(OnTargetHandler onTargetHandler) {
        this.onTargetHandler = onTargetHandler;
    }

    public double getMinSetpoint() {
        return minSetpoint;
    }

    public void setMinSetpoint(double minSetpoint) {
        this.minSetpoint = minSetpoint;
    }

    public double getMaxSetpoint() {
        return maxSetpoint;
    }

    public void setMaxSetpoint(double maxSetpoint) {
        this.maxSetpoint = maxSetpoint;
    }

    public double getMinOutput() {
        return minOutput;
    }

    public void setMinOutput(double minOutput) {
        this.minOutput = minOutput;
    }

    public double getMaxOutput() {
        return maxOutput;
    }

    public void setMaxOutput(double maxOutput) {
        this.maxOutput = maxOutput;
    }

    public synchronized double getSetpoint() {
        return setpoint;
    }

    public synchronized void setSetpoint(double setpoint) {
        this.setpoint = applyLimits(setpoint, minSetpoint, maxSetpoint);
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

    public synchronized LimitState getLimitState() {
        return limitState;
    }

    @Override
    public void onEnable() {
        checkNotNull(inputProvider, "input must be set on a controller before using");
        checkNotNull(outputConsumer, "output must be set on a controller before using");
    }

    @Override
    public void onDisable() {
        if (outputConsumer != null)
            outputConsumer.setControllerOutput(0.0);
    }

    protected void configure(Config config) {
        if (!Double.isNaN(config.maxAbsoluteSetpoint)) {
            setMaxSetpoint(config.maxAbsoluteSetpoint);
            setMinSetpoint(-config.maxAbsoluteSetpoint);
        } else {
            setMaxSetpoint(config.maxSetpoint);
            setMinSetpoint(config.minSetpoint);
        }
        if (!Double.isNaN(config.maxAbsoluteOutput)) {
            setMaxOutput(config.maxAbsoluteOutput);
            setMinOutput(-config.maxAbsoluteOutput);
        } else {
            setMinOutput(config.minOutput);
            setMaxOutput(config.maxOutput);
        }
        configureTarget(config.target);
    }

    protected static class Config {
        public double maxAbsoluteSetpoint = Double.NaN;
        public double maxSetpoint = Double.POSITIVE_INFINITY, minSetpoint = Double.NEGATIVE_INFINITY;
        public double maxAbsoluteOutput = Double.NaN;
        public double maxOutput = Double.POSITIVE_INFINITY, minOutput = Double.NEGATIVE_INFINITY;
        public JsonNode target;
    }
}
