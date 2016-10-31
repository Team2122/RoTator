package org.teamtators.rotator.operatorInterface;

public class DriveOutput {
    private double left;
    private double right;

    public DriveOutput() {
        this(0.0, 0.0);
    }

    public DriveOutput(double left, double right) {
        this.left = left;
        this.right = right;
    }

    public static DriveOutput straight(double speed) {
        return new DriveOutput(speed, speed);
    }

    public static DriveOutput turning(double speed) {
        return new DriveOutput(speed, -speed);
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public DriveOutput neg() {
        return new DriveOutput(-left, -right);
    }

    public DriveOutput plus(DriveOutput other) {
        return new DriveOutput(left + other.left, right + other.right);
    }

    public DriveOutput minus(DriveOutput other) {
        return plus(other.neg());
    }

    public DriveOutput mul(double leftFactor, double rightFactor) {
        return new DriveOutput(left * leftFactor, right * rightFactor);
    }

    public DriveOutput mul(DriveOutput factors) {
        return mul(factors.left, factors.right);
    }

    public DriveOutput mul(double factor) {
        return mul(factor, factor);
    }

    public DriveOutput deadzone(double deadzone) {
        return new DriveOutput(DriveUtils.applyDeadzone(left, deadzone),
                DriveUtils.applyDeadzone(right, deadzone));
    }

    public DriveOutput exponent(double exponent) {
        return new DriveOutput(DriveUtils.applyExponent(left, exponent),
                DriveUtils.applyExponent(right, exponent));
    }
}
