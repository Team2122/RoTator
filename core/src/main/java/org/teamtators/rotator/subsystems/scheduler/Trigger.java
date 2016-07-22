package org.teamtators.rotator.subsystems.scheduler;

public interface Trigger {

    /**
     * Returns whether or not the trigger is active
     *
     * This method will be called repeatedly.
     *
     * @return whether or not the trigger condition is active.
     */
     boolean getActive();
}
