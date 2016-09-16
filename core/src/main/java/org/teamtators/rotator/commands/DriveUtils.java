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
        if (input == 0 || multiplier == 0 || Math.abs(input) - deadzone < 0) {
            return 0;
        }
        return Math.pow(input * multiplier, exponent);
    }
}
