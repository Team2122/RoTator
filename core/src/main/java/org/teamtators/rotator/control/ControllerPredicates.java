package org.teamtators.rotator.control;

import static com.google.common.base.Preconditions.checkNotNull;

public class ControllerPredicates {
    public static ControllerPredicate staticValue(boolean value) {
        return (controller) -> value;
    }

    public static ControllerPredicate alwaysTrue() {
        return staticValue(true);
    }

    public static ControllerPredicate alwaysFalse() {
        return staticValue(false);
    }

    public static ControllerPredicate withinError(double threshold) {
        return (controller) -> Math.abs(controller.getError()) < threshold;
    }

    public static ControllerPredicate sampleWithinError(double time, double threshold) {
        return new SampleTime(time, withinError(threshold));
    }

    public static class SampleTime implements ControllerPredicate {
        private double currentTime;
        private double time;
        private ControllerPredicate baseChecker;

        public SampleTime(double time, ControllerPredicate baseChecker) {
            this.time = time;
            this.baseChecker = checkNotNull(baseChecker);
        }

        @Override
        public boolean compute(AbstractController controller) {
            if (baseChecker.compute(controller)) {
                currentTime += controller.getLastDelta();
                if (currentTime > time) {
                    return true;
                }
            } else {
                currentTime = 0.0;
            }
            return false;
        }
    }
}
