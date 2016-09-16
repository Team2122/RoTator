package org.teamtators.rotator.commands;

/**
 * Utilities for DriveTank input
 */
public class DriveUtils {
    /**
     * Apply modifiers to drive input
     *
     * @param input      Original input
     * @param deadzone   Zone in which to ignore input
     * @param multiplier Value to multiply input by
     * @param exponent   Value to raise input to the power of
     * @return Modified input
     */
    public static double applyDriveModifiers(double input, double deadzone, double multiplier, double exponent) {
        input = applyDeadzone(input * multiplier, deadzone);
        return applyExponent(input, exponent);
    }

    public static double applyExponent(double input, double exponent) {
        double absolute = Math.abs(input);
        double sign = Math.signum(input);
        return sign * Math.pow(absolute, exponent);
    }

    public static double applyDeadzone(double input, double deadzone) {
        if (Math.abs(input) <= deadzone)
            return 0;
        return input;
    }
}
