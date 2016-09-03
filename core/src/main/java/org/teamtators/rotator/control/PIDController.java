package org.teamtators.rotator.control;

/**
 * A PID Controller implementation
 */
public class PIDController extends PController {
    private double kI;
    private double kD;
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

    public PIDController(String name, double kP, double kI, double kD, double maxError) {
        this(name, kP, kI, kD);
        this.maxError = maxError;
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

    @Override
    protected double computeOutput(double delta) {
        double tr = super.computeOutput(delta);
        if (getError() < maxError) {
            tr += kI * totalError;
        }
        if (lastInput != Double.NaN && delta != 0) {
            tr += kD * (getInput() - lastInput) / delta;
        }

        lastInput = getInput();
        totalError += getError() * delta;

        return tr;
    }
}
