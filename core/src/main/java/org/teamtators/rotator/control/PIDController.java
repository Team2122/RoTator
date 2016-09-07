package org.teamtators.rotator.control;

/**
 * A PID Controller implementation
 */
public class PIDController extends PController {
    private double kI;
    private double kD;
    private double kF;
    private double maxError = Double.POSITIVE_INFINITY;

    private double lastInput = Double.NaN;
    private double totalError = 0;

    public PIDController(String name) {
        super(name);
    }

    public PIDController(String name, double kP, double kI, double kD) {
        super(name, kP);
        setkI(kI);
        setkD(kD);
    }

    public PIDController(String name, double kP, double kI, double kD, double kF, double maxError) {
        this(name, kP, kI, kD);
        setkF(kF);
        setMaxError(maxError);
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

    public double getMaxError() {
        return maxError;
    }

    public void setMaxError(double maxError) {
        this.maxError = maxError;
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
        if (getError() < maxError) {
            output += kI * totalError;
        }
        if (lastInput != Double.NaN && delta != 0) {
            output += kD * (getInput() - lastInput) / delta;
        }
        output += kF*getSetpoint();

        lastInput = getInput();
        totalError += getError() * delta;

        return output;
    }
}
