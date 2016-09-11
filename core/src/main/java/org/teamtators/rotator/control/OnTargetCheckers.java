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
}
