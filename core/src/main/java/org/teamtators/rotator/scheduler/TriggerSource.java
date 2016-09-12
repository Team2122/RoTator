package org.teamtators.rotator.scheduler;

public interface TriggerSource {

    /**
     * Returns whether or not the trigger is active
     * <p>
     * This method will be called repeatedly.
     *
     * @return whether or not the trigger condition is active.
     */
    boolean getActive();
}
