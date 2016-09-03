package org.teamtators.rotator.control;

/**
 * A controller that scales the error by a value
 */
public class PController extends AbstractController {
    private double kP;

    public PController(String name) {
        super(name);
    }

    public PController(String name, double kP) {
        super(name);
        setkP(kP);
    }

    @Override
    protected double computeOutput(double delta) {
        return getError() * kP;
    }

    public void setkP(double kP) {
        this.kP = kP;
    }

    public double getkP() {
        return kP;
    }
}
