package org.teamtators.rotator.control;

import org.teamtators.rotator.config.Configurable;

/**
 * A controller for motion with a trapezoidal velocity graph
 */
public class TrapezoidController extends AbstractController implements Configurable<TrapezoidController.Config> {
    private double startSpeed;
    private double endSpeed;
    private double maxVelocity;
    private double accelerationEnd;
    private double decelerationStart;
    private double decelerationEnd;

    public TrapezoidController(String name) {
        super(name);
    }

    public TrapezoidController() {
        this("TrapezoidController");
    }

    @Override
    protected double computeOutput(double delta) {
        double input = getInput();
        if (input < accelerationEnd) {
            return startSpeed
                    + (maxVelocity - startSpeed) * (input / accelerationEnd);
        } else if (input >= accelerationEnd && input < decelerationStart) {
            return maxVelocity;
        } else if (input >= decelerationStart && input < decelerationEnd) {
            return maxVelocity - (maxVelocity - endSpeed)
                    * (input - decelerationStart)
                    / (decelerationEnd - decelerationStart);
        } else {
            return endSpeed;
        }
    }

    @Override
    public void configure(Config config) {
        startSpeed = config.startSpeed;
        endSpeed = config.endSpeed;
        maxVelocity = config.maxVelocity;
        accelerationEnd = config.accelerationEnd;
        decelerationStart = config.decelerationStart;
        decelerationEnd = config.decelerationEnd;
        super.configure(config);
    }

    static class Config extends AbstractController.Config {
        public double startSpeed = 0;
        public double endSpeed = 0;
        public double maxVelocity;
        public double accelerationEnd = 0;
        public double decelerationStart;
        public double decelerationEnd;
    }
}
