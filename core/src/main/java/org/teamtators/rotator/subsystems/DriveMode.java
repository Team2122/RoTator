package org.teamtators.rotator.subsystems;

/**
 * The different drive modes drive train can be in
 */
public enum DriveMode {
    DISABLED,
    /**
     * Direct is when the speed value directly corresponds to the motor
     */
    DIRECT,
    /**
     * CONTROLLER is when the motors are controlled with the CONTROLLER loop
     */
    VELOCITY
}