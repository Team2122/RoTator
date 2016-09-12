package org.teamtators.rotator.scheduler;

/**
 * Interface for classes that need to be notified on change of RobotState
 */
public interface StateListener {
    /**
     * Callback for robot state change
     *
     * @param newState New robot state
     */
    void onEnterState(RobotState newState);
}
