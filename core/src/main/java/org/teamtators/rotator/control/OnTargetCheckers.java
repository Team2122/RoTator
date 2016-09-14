package org.teamtators.rotator.control;

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

    public static class SampleTime implements OnTargetChecker {

        private double currentTime;
        private double setTime;
        private OnTargetChecker baseChecker;

        public SampleTime(double setTime, OnTargetChecker baseChecker) {
            this.setTime = setTime;
            this.baseChecker = baseChecker;
        }

        @Override
        public boolean compute(double delta, AbstractController controller) {
            if(baseChecker.compute(delta, controller)) {
                currentTime += delta;
                if(currentTime > setTime) {
                    return true;
                }
            }
            else {
                currentTime = 0;
            }
            return false;
        }
    }
}
