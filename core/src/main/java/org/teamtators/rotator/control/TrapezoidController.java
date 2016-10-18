package org.teamtators.rotator.control;

import org.teamtators.rotator.config.ConfigException;
import org.teamtators.rotator.config.Configurable;
import org.teamtators.rotator.datalogging.LogDataProvider;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * A controller for motion with a trapezoidal velocity graph
 */
public class TrapezoidController extends AbstractController implements Configurable<TrapezoidController.Config> {
    private double startVelocity;
    private double endVelocity;
    private double travelVelocity;
    private double maxAcceleration;

    private boolean isTrapezoidal;
    private double time;
    private double totalTime;
    private double startSectionTime;
    private double flatTime;
    private DataProvider dataProvider;

    public TrapezoidController(String name) {
        super(name);
        time = 0;
        setTargetPredicate(getTrapezoidTargetPredicate());
    }

    @Inject
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

    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMaxAcceleration(double maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
    }

    @Override
    public void onEnable() {
        startSectionTime = Math.abs(travelVelocity - startVelocity) / maxAcceleration;
        double endSectionTime = Math.abs(travelVelocity - endVelocity) / maxAcceleration;
        double startDistance = startSectionTime * (travelVelocity + startVelocity) / 2;
        double endDistance = endSectionTime * (travelVelocity + endVelocity) / 2;
        double flatDistance = getSetpoint() - (startDistance + endDistance);
        flatTime = flatDistance / travelVelocity;
        isTrapezoidal = flatTime >= 0;
        if (!isTrapezoidal) {
            totalTime = Math.sqrt(getSetpoint() / maxAcceleration) * 2;
        } else {
            totalTime = startSectionTime + flatTime + endSectionTime;
        }
        time = 0;
        super.onEnable();
    }

    @Override
    protected double computeOutput(double delta) {
        time += delta;
        double velocity;
        if (isTrapezoidal) {
            if (time < startSectionTime) {
                // accelerating
                velocity = Math.signum(travelVelocity - startVelocity) * maxAcceleration * time + startVelocity;
            } else if (time >= startSectionTime + flatTime) {
                // decelerating
                velocity = Math.signum(endVelocity - travelVelocity) * maxAcceleration
                        * (time - (startSectionTime + flatTime)) + travelVelocity;
            } else {
                // constant velocity
                velocity = travelVelocity;
            }
        } else {
            if (time < totalTime / 2) {
                velocity = Math.signum(travelVelocity - startVelocity) * maxAcceleration * time + startVelocity;
            } else {
                velocity = Math.signum(endVelocity - travelVelocity) * maxAcceleration * (totalTime - time) + travelVelocity;
            }
        }
        return velocity;
    }

    @Override
    public void configure(Config config) {
        travelVelocity = config.travelVelocity;
        maxAcceleration = config.maxAcceleration;
        startVelocity = config.startVelocity;
        endVelocity = config.endVelocity;
        if (config.maxAcceleration <= 0) {
            throw new ConfigException("Absolute value of maxAcceleration must be a positive number");
        }
        if (config.travelVelocity == 0) {
            throw new ConfigException("Travel velocity must not be 0");
        }
        super.configure(config);
    }

    @Override
    public LogDataProvider getLogDataProvider() {
        if (dataProvider == null)
            dataProvider = new DataProvider();
        return dataProvider;
    }

    public static class Config extends AbstractController.Config {
        public double startVelocity = 0;
        public double endVelocity = 0;
        public double travelVelocity;
        public double maxAcceleration;
    }

    protected class DataProvider implements LogDataProvider {
        @Override
        public String getName() {
            return TrapezoidController.this.getName();
        }

        @Override
        public List<Object> getKeys() {
            return Arrays.asList("time", "setpoint", "input", "output");
        }

        @Override
        public List<Object> getValues() {
            return Arrays.asList(time, getSetpoint(), getInput(), getOutput());
        }
    }

    public ControllerPredicate getTrapezoidTargetPredicate() {
        return new TrapezoidTargetPredicate();
    }

    private class TrapezoidTargetPredicate implements ControllerPredicate {
        @Override
        public boolean compute(AbstractController controller) {
            return time >= totalTime;
        }
    }
}
