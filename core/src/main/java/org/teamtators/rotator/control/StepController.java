package org.teamtators.rotator.control;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamtators.rotator.config.Configurable;

public class StepController extends AbstractController implements Configurable<StepController.Config> {

    private double highSpeed;
    private double lowSpeed;

    private double lowThreshold;
    private double highThreshold;

    public StepController() {
        super();
        setTargetPredicate(getLowThresholdPredicate());
    }

    public StepController(String name, double highSpeed, double lowSpeed, double lowThreshold, double highThreshold) {
        super(name);
        this.highSpeed = highSpeed;
        this.lowSpeed = lowSpeed;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }

    private class LowThresholdPredicate implements ControllerPredicate {
        @Override
        public boolean compute(double delta, AbstractController controller) {
            return Math.abs(controller.getError()) < lowThreshold;
        }
    }

    public ControllerPredicate getLowThresholdPredicate() {
        return new LowThresholdPredicate();
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

    @Override
    public void configure(Config config) {
        highSpeed = config.highSpeed;
        lowSpeed = config.lowSpeed;
        lowThreshold = config.lowThreshold;
        highThreshold = config.highThreshold;
        configureTarget(config.target);
    }

    public static class Config {
        public double highSpeed, lowSpeed, lowThreshold, highThreshold;
        public JsonNode target;
    }
}
