package org.teamtators.rotator.control;

public class SystemNanoTimeTimeProvider implements ITimeProvider {
    @Override
    public long currentTimeMillis() {
        return System.nanoTime() / 1000000;
    }
}
