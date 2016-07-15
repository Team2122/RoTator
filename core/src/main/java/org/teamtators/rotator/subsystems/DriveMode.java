package org.teamtators.rotator.subsystems;

/**
 * The different drive modes drive train can be in
 */
public enum DriveMode {
    /**
     * Direct is when the power value directly corresponds to the motor
     */
    DIRECT,
    /**
     * PID is when the motors are controlled with the PID loop
     */
    PID
}