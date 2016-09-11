package org.teamtators.rotator.control;

/**
 * A controller that scales the error by a value
 */
public class PController extends AbstractController {
    private double kP = 0.0;

    public PController() {
        super();
    }

    public PController(String name, double kP) {
        super(name);
        setkP(kP);
    }

    @Override
    protected double computeOutput(double delta) {
        return getError() * kP;
    }

    public synchronized void setkP(double kP) {
        this.kP = kP;
    }

    public synchronized double getkP() {
        return kP;
    }
}
