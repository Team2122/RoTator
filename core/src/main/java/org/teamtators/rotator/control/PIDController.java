package org.teamtators.rotator.control;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.config.Configurable;

import javax.inject.Inject;

/**
 * A PID Controller implementation
 */
public class PIDController extends AbstractController implements Configurable<PIDController.Config> {
    private double kP = 0.0;
    private double kI = 0.0;
    private double kD = 0.0;
    private double kF = 0.0;
    private double maxIError = Double.POSITIVE_INFINITY;

    private double lastInput;
    private double totalError;

    @Inject
    public PIDController() {
        super();
    }

    public PIDController(String name, double kP, double kI, double kD) {
        super(name);
        setP(kP);
        setI(kI);
        setD(kD);
    }

    public PIDController(String name, double kP, double kI, double kD, double kF, double maxError) {
        this(name, kP, kI, kD);
        setF(kF);
        setMaxIError(maxError);
    }

    public synchronized void setPIDF(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public void setPID(double kP, double kI, double kD) {
        setPIDF(kP, kI, kD, 0.0);
    }

    public synchronized double getP() {
        return kP;
    }

    public synchronized void setP(double kP) {
        this.kP = kP;
    }

    public synchronized double getI() {
        return kI;
    }

    public synchronized void setI(double kI) {
        this.kI = kI;
    }

    public synchronized double getD() {
        return kD;
    }

    public synchronized void setD(double kD) {
        this.kD = kD;
    }

    public synchronized double getMaxIError() {
        return maxIError;
    }

    public synchronized void setMaxIError(double maxIError) {
        this.maxIError = maxIError;
    }

    public synchronized double getF() {
        return kF;
    }

    public synchronized void setF(double kF) {
        this.kF = kF;
    }

    @Override
    protected double computeOutput(double delta) {
        double error = getError();
        double output = error * kP;
        if (Math.abs(error) < maxIError) {
            output += kI * totalError;
        }
        if (!Double.isNaN(lastInput) && delta != 0) {
            output += kD * (getInput() - lastInput) / delta;
        }
        output += kF * getSetpoint();

        lastInput = getInput();
        totalError += error * delta;

        if (Double.isInfinite(output) || Double.isNaN(output))
            return 0;

        return output;
    }

    public synchronized void resetTotalError() {
        totalError = 0;
    }

    @Override
    public void reset() {
        super.reset();
        synchronized (this) {
            lastInput = Double.NaN;
        }
        resetTotalError();
    }

    @Override
    public void configure(Config config) {
        if (config == null) return;
        setPIDF(config.P, config.I, config.D, config.F);
        setMaxIError(config.maxI);
        configureTarget(config.target);
    }

    public static class Config {
        public double P = 0.0, I = 0.0, D = 0.0, F = 0.0, maxI = Double.POSITIVE_INFINITY;
        public JsonNode target;
    }
}
