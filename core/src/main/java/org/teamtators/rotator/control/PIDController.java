package org.teamtators.rotator.control;

/**
 * A PID Controller implementation
 */
public class PIDController extends PController {
    private double kI;
    private double kD;
    private double kF;
    private double maxIError = Double.POSITIVE_INFINITY;

    private double lastInput;
    private double totalError;

    public PIDController(String name) {
        super(name);
        reset();
    }

    public PIDController(String name, double kP, double kI, double kD) {
        super(name, kP);
        setkI(kI);
        setkD(kD);
        reset();
    }

    public PIDController(String name, double kP, double kI, double kD, double kF, double maxError) {
        this(name, kP, kI, kD);
        setkF(kF);
        setMaxIError(maxError);
    }

    public double getkI() {
        return kI;
    }

    public void setkI(double kI) {
        this.kI = kI;
    }

    public double getkD() {
        return kD;
    }

    public void setkD(double kD) {
        this.kD = kD;
    }

    public double getMaxIError() {
        return maxIError;
    }

    public void setMaxIError(double maxIError) {
        this.maxIError = maxIError;
    }

    public double getkF() {
        return kF;
    }

    public void setkF(double kF) {
        this.kF = kF;
    }

    @Override
    protected double computeOutput(double delta) {
        double output = super.computeOutput(delta);
        if (getError() < maxIError) {
            output += kI * totalError;
        }
        if (lastInput != Double.NaN && delta != 0) {
            output += kD * (getInput() - lastInput) / delta;
        }
        output += kF * getSetpoint();

        lastInput = getInput();
        totalError += getError() * delta;

        return output;
    }

    public void resetTotalError() {
        totalError = 0;
    }

    @Override
    public void reset() {
        super.reset();
        lastInput = Double.NaN;
        resetTotalError();
    }
}
