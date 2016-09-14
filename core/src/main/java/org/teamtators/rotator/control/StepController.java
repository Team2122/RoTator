package org.teamtators.rotator.control;

import org.teamtators.rotator.config.Configurable;

public class StepController extends AbstractController implements Configurable<StepController.Config> {

    private double highSpeed;
    private double lowSpeed;

    private double lowThreshold;
    private double highThreshold;

    public StepController(String name) {
        super(name);
        setTargetChecker(((delta, controller) -> ((StepController) controller).lowThreshold > controller.getError()));
    }

    @Override
    protected double computeOutput(double delta) {
        double absError = Math.abs(getError());
        double sign = Math.signum(getError());
        if (absError > highThreshold) {
            return highSpeed * sign;
        } else if (absError > lowThreshold) {
            return lowSpeed * sign;
        } else {
            return 0;
        }
    }

    public StepController(String name, double highSpeed, double lowSpeed, double lowThreshold, double highThreshold) {
        super(name);
        this.highSpeed = highSpeed;
        this.lowSpeed = lowSpeed;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }

    @Override
    public void configure(Config config) {
        highSpeed = config.highSpeed;
        lowSpeed = config.lowSpeed;
        lowThreshold = config.lowThreshold;
        highThreshold = config.highThreshold;
    }

    public static class Config {
        public double highSpeed, lowSpeed, lowThreshold, highThreshold;
    }
}
