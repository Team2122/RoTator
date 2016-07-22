package org.teamtators.rotator.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamtators.rotator.operatorInterface.LogitechF310;

/**
 * Class to represent a component test
 */
public abstract class ComponentTest {
    private String name;
    protected Logger logger;

    /**
     * Construct a new ComponentTest with given name
     *
     * @param name Name for the ComponentTest
     */
    public ComponentTest(String name) {
        setName(name);
    }

    /**
     * Executed when the test is selected
     */
    public void start() {
        logger.info("Starting {} {}", getClass(), getName());
    }

    /**
     * Executed repeatedly while test is selected
     */
    public void step() {
    }

    /**
     * Called when a button is pressed
     *
     * @param button The button that was pressed
     */
    public void onButtonDown(LogitechF310.Button button) {
    }

    /**
     * Called when a button is released
     *
     * @param button The button that was released
     */
    public void onButtonUp(LogitechF310.Button button) {
    }

    /**
     * Called repeatedly with axis values
     *
     * @param axis  The axis to update
     * @param value The value of the axis
     */
    public void updateAxis(LogitechF310.Axis axis, double value) {
    }

    /**
     * Executed when the test is stopped (navigated away from)
     */
    public void stop() {
    }

    /**
     * @return the name of the test
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the test
     *
     * @param name the name of the test
     */
    public void setName(String name) {
        this.name = name;
        String loggerName = String.format("%s(%s)", this.getClass().getName(), name);
        this.logger = LoggerFactory.getLogger(loggerName);
    }
}