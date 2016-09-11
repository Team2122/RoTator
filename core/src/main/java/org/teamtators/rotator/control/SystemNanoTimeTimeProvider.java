package org.teamtators.rotator.control;

public class SystemNanoTimeTimeProvider implements ITimeProvider {
    private static final double NS_TO_S = 1e+9;

    @Override
    public double getTimestamp() {
        return System.nanoTime() / NS_TO_S;
    }
}
