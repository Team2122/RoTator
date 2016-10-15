package org.teamtators.rotator.control;

import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurable;

/**
 * A controller for motion with a trapezoidal velocity graph
 */
public class TrapezoidController extends AbstractController implements Configurable<TrapezoidController.Config> {
    private double startVelocity;
    private double endVelocity;
    private double travelVelocity;
    private double maxAbsAcceleration;

    private boolean isTrapezoidal;
    private double time;
    private double totalTime;
    private double startSectionTime;
    private double flatTime;

    public TrapezoidController(String name) {
        super(name);
        time = 0;
    }

    public TrapezoidController() {
        this("TrapezoidController");
    }

    public double getStartVelocity() {
        return startVelocity;
    }

    public void setStartVelocity(double startVelocity) {
        this.startVelocity = startVelocity;
    }

    public double getEndVelocity() {
        return endVelocity;
    }

    public void setEndVelocity(double endVelocity) {
        this.endVelocity = endVelocity;
    }

    public double getTravelVelocity() {
        return travelVelocity;
    }

    public void setTravelVelocity(double travelVelocity) {
        this.travelVelocity = travelVelocity;
    }

    public double getMaxAbsAcceleration() {
        return maxAbsAcceleration;
    }

    public void setMaxAbsAcceleration(double maxAbsAcceleration) {
        this.maxAbsAcceleration = maxAbsAcceleration;
    }

    @Override
    public void onEnable() {
        startSectionTime = Math.abs(travelVelocity - startVelocity) / maxAbsAcceleration;
        double endSectionTime = Math.abs(travelVelocity - endVelocity) / maxAbsAcceleration;
        double startDistance = startSectionTime * (travelVelocity + startVelocity) / 2;
        double endDistance = endSectionTime * (travelVelocity + endVelocity) / 2;
        double flatDistance = getSetpoint() - (startDistance + endDistance);
        flatTime = flatDistance / travelVelocity;
        isTrapezoidal = flatTime >= 0;
        if (!isTrapezoidal) {
            totalTime = Math.sqrt(getSetpoint() / maxAbsAcceleration) * 2;
        }
        super.onEnable();
    }

    @Override
    protected double computeOutput(double delta) {
        time += delta;
        double velocity;
        if (isTrapezoidal) {
            if (time < startSectionTime) {
                // accelerating
                velocity = Math.signum(travelVelocity - startVelocity) * maxAbsAcceleration * time + startVelocity;
            } else if (time >= startSectionTime + flatTime) {
                // decelerating
                velocity = Math.signum(endVelocity - travelVelocity) * maxAbsAcceleration
                        * (time - (startSectionTime + flatTime)) + endVelocity;
            } else {
                // constant velocity
                velocity = travelVelocity;
            }
        } else {
            if (time < totalTime / 2) {
                velocity = Math.signum(travelVelocity - startVelocity) * maxAbsAcceleration * time + startVelocity;
            } else {
                velocity = Math.signum(endVelocity - travelVelocity) * maxAbsAcceleration * time + endVelocity;
            }
        }
        return velocity;
    }

    @Override
    public void configure(Config config) {
        travelVelocity = config.travelVelocity;
        maxAbsAcceleration = config.maxAbsAcceleration;
        startVelocity = config.startVelocity;
        endVelocity = config.endVelocity;
        if (maxAbsAcceleration <= 0) {
            throw new ConfigException("Absolute value of maxAbsAcceleration must be a positive number");
        }
        if (travelVelocity == 0) {
            throw new ConfigException("Travel velocity must not be 0");
        }
        super.configure(config);
    }

    static class Config extends AbstractController.Config {
        public double startVelocity = 0;
        public double endVelocity = 0;
        public double travelVelocity;
        public double maxAbsAcceleration;
    }
}
