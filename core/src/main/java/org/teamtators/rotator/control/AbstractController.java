package org.teamtators.rotator.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.EvictingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.datalogging.DataCollector;
import org.teamtators.rotator.datalogging.DataLoggable;
import org.teamtators.rotator.datalogging.LogDataProvider;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractController extends AbstractSteppable implements DataLoggable {
    protected Logger logger;
    @Inject
    DataCollector dataCollector;
    private String name;
    private ControllerInputProvider inputProvider;
    private int inputSamplesToAverage;
    private EvictingQueue<Double> inputSampleQueue;
    private ControllerOutputConsumer outputConsumer;
    private ControllerPredicate targetPredicate = ControllerPredicates.alwaysFalse();
    private LimitPredicate limitPredicate = LimitPredicates.neverAtLimit();
    private boolean stopOnTarget = false;
    private OnTargetHandler onTargetHandler = null;
    private boolean dataLog = false;

    private double minSetpoint = Double.NEGATIVE_INFINITY;
    private double maxSetpoint = Double.POSITIVE_INFINITY;
    private double minOutput = Double.NEGATIVE_INFINITY;
    private double maxOutput = Double.POSITIVE_INFINITY;

    private volatile double lastDelta;
    private volatile double setpoint;
    private volatile double input;
    private volatile double output;
    private volatile boolean onTarget;
    private volatile LimitState limitState;
    private LogDataProvider logDataProvider = null;

    public AbstractController() {
        this("");
    }

    public AbstractController(String name) {
        reset();
        setInputSamplesToAverage(1);
        setName(name);
        setExecutionOrder(200);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value > max) return max;
        else if (value < min) return min;
        else return value;
    }

    public synchronized void reset() {
        disable();
        clearInputSamples();
        lastDelta = 0.0;
        setpoint = 0.0;
        input = 0.0;
        output = 0.0;
        onTarget = false;
        limitState = LimitState.NEITHER;
    }

    @Override
    public final void step(double delta) {
        synchronized (this) {
            this.lastDelta = delta;
            double computedInput = this.inputProvider.getControllerInput();
            if (inputSamplesToAverage == 1) {
                input = computedInput;
            } else {
                inputSampleQueue.add(computedInput);
                input = inputSampleQueue.stream().mapToDouble(d -> d).average().orElse(0.0);
            }
            onTarget = targetPredicate.compute(this);
            limitState = limitPredicate.getLimit(this);
        }

        if (onTarget && onTargetHandler != null) {
            onTargetHandler.onTarget(this);
        }

        double computedOutput;
        if (onTarget && stopOnTarget) {
            computedOutput = 0;
        } else {
            computedOutput = computeOutput(delta);
        }
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

    public int getInputSamplesToAverage() {
        return inputSamplesToAverage;
    }

    public void setInputSamplesToAverage(int inputSamplesToAverage) {
        this.inputSamplesToAverage = inputSamplesToAverage;
        this.inputSampleQueue = EvictingQueue.create(inputSamplesToAverage);
    }

    public void clearInputSamples() {
        if (inputSampleQueue != null)
            this.inputSampleQueue.clear();
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

    public boolean isStopOnTarget() {
        return stopOnTarget;
    }

    public void setStopOnTarget(boolean stopOnTarget) {
        this.stopOnTarget = stopOnTarget;
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
        if (config.has("disable")) {
            OnTargetHandler onTargetHandler = null;
            if (config.get("disable").asBoolean()) {
                onTargetHandler = OnTargetHandlers.disableController();
            }
            setOnTargetHandler(onTargetHandler);
        }
        if (config.has("stop")) {
            setStopOnTarget(config.get("stop").asBoolean());
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

    public synchronized double getLastDelta() {
        return lastDelta;
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
        checkNotNull(inputProvider, "input must be set on a angleController before using");
        checkNotNull(outputConsumer, "output must be set on a angleController before using");
        if (isDataLogging()) {
            dataCollector.startProvider(getLogDataProvider());
        }
        if (inputProvider instanceof AbstractSteppable)
            ((AbstractSteppable) inputProvider).enable();
    }

    @Override
    public void onDisable() {
        if (outputConsumer != null)
            outputConsumer.setControllerOutput(0.0);
        dataCollector.stopProvider(getLogDataProvider());
        if (inputProvider instanceof AbstractSteppable)
            ((AbstractSteppable) inputProvider).disable();
    }

    protected void configure(Config config) {
        setInputSamplesToAverage(config.inputSamplesToAverage);
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
        setDataLogging(config.dataLogging);
    }

    @Override
    public LogDataProvider getLogDataProvider() {
        if (logDataProvider == null) logDataProvider = new ControllerLogDataProvider();
        return logDataProvider;
    }

    public boolean isDataLogging() {
        return dataLog;
    }

    public void setDataLogging(boolean dataLogging) {
        this.dataLog = dataLogging;
        boolean enabled = isEnabled();
        if (dataLogging) {
            if (enabled) {
                dataCollector.startProvider(getLogDataProvider());
            }
        } else {
            dataCollector.stopProvider(getLogDataProvider());
        }
    }

    public double getOutput() {
        return output;
    }

    protected static class Config {
        public int inputSamplesToAverage = 1;
        public double maxAbsoluteSetpoint = Double.NaN;
        public double maxSetpoint = Double.POSITIVE_INFINITY, minSetpoint = Double.NEGATIVE_INFINITY;
        public double maxAbsoluteOutput = Double.NaN;
        public double maxOutput = Double.POSITIVE_INFINITY, minOutput = Double.NEGATIVE_INFINITY;
        public JsonNode target;
        public boolean dataLogging = false;
    }

    protected class ControllerLogDataProvider implements LogDataProvider {
        @Override
        public String getName() {
            return AbstractController.this.getName();
        }

        @Override
        public List<Object> getKeys() {
            return Arrays.asList("setpoint", "input", "output", "onTarget");
        }

        @Override
        public List<Object> getValues() {
            return Arrays.asList(getSetpoint(), getInput(), output, isOnTarget());
        }
    }
}
