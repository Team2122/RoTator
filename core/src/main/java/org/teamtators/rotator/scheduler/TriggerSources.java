package org.teamtators.rotator.scheduler;

public class TriggerSources {
    public static TriggerSource constant(boolean active) {
        return () -> active;
    }
}
