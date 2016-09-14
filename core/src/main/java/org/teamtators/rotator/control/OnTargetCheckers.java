package org.teamtators.rotator.control;

import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;

public class OnTargetCheckers {
    public static OnTargetChecker staticValue(boolean value) {
        return (delta, controller) -> value;
    }

    public static OnTargetChecker alwaysOnTarget() {
        return staticValue(true);
    }

    public static OnTargetChecker neverOnTarget() {
        return staticValue(false);
    }

    public static OnTargetChecker withinError(double threshold) {
        return (delta, controller) -> controller.getError() < threshold;
    }

    public static OnTargetChecker sampleWithinError(double time, double threshold) {
        return new SampleTime(time, withinError(threshold));
    }

    public static class SampleTime implements OnTargetChecker {
        private double currentTime;
        private double time;
        private OnTargetChecker baseChecker;

        public SampleTime(double time, OnTargetChecker baseChecker) {
            this.time = time;
            this.baseChecker = checkNotNull(baseChecker);
        }

        @Override
        public boolean compute(double delta, AbstractController controller) {
            if (baseChecker.compute(delta, controller)) {
                currentTime += delta;
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
