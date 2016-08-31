package org.teamtators.rotator.tester;

import com.google.inject.Inject;
import org.teamtators.rotator.operatorInterface.AbstractOperatorInterface;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.RobotState;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A class that allows manually running ComponentTest's
 */
public class ManualTester extends Command {
    private int testGroupIndex = 0;
    private int testIndex = 0;

    private LogitechF310 joystick;

    private Map<LogitechF310.Button, Boolean> lastStates = new EnumMap<LogitechF310.Button, Boolean>(LogitechF310.Button.class);

    public ManualTester() {
        super("ManualTester");
        validIn(RobotState.TEST);
    }

    @Inject
    public void setJoystick(AbstractOperatorInterface joystick) {
        this.joystick = joystick.driverJoystick();
    }

    @Override
    protected void initialize() {
        lastStates.clear();
    }

    @Override
    protected boolean step() {
        getCurrentTest().step();
        getCurrentTest().updateAxis(LogitechF310.Axis.RIGHT_STICK_Y,
                joystick.getAxisValue(LogitechF310.Axis.RIGHT_STICK_Y));
        for (LogitechF310.Button button : LogitechF310.Button.values()) {
            boolean value = joystick.getButtonValue(button);
            if (lastStates.containsKey(button)) {
                boolean lastValue = lastStates.get(button);
                if (value && !lastValue) {
                    if (button == LogitechF310.Button.POV_DOWN) {
                        nextTestGroup();
                    } else if (button == LogitechF310.Button.POV_UP) {
                        previousTestGroup();
                    } else if (button == LogitechF310.Button.POV_LEFT) {
                        previousTest();
                    } else if (button == LogitechF310.Button.POV_RIGHT) {
                        nextTest();
                    } else {
                        getCurrentTest().onButtonDown(button);
                    }
                } else if (lastValue && !value) {
                    getCurrentTest().onButtonUp(button);
                }
            }
            lastStates.put(button, value);
        }
        return false;
    }

    private ComponentTest getCurrentTest() {
        return getCurrentTestGroup().get(testIndex);
    }

    private List<ComponentTest> getCurrentTestGroup() {
        return testGroups.get(testGroupIndex);
    }

    @Override
    protected void finish(boolean interrupted) {
        getCurrentTest().stop();
    }

    public void nextTest() {
        getCurrentTest().stop();
        testIndex++;
        if (testIndex >= getCurrentTestGroup().size()) {
            testIndex = 0;
        }
        startNextTest();
    }

    public void previousTest() {
        getCurrentTest().stop();
        testIndex--;
        if (testIndex < 0) {
            testIndex = getCurrentTestGroup().size() - 1;
        }
        startNextTest();
    }

    public void nextTestGroup() {
        getCurrentTest().stop();
        testGroupIndex++;
        if (testGroupIndex >= testGroups.size()) {
            testGroupIndex = 0;
        }
        startNextTest();
    }

    public void previousTestGroup() {
        getCurrentTest().stop();
        testGroupIndex--;
        if (testGroupIndex < 0) {
            testGroupIndex = testGroups.size() - 1;
        }
        startNextTest();
    }

    private void startNextTest() {
        logger.info("Starting {}", getCurrentTest().getName());
        getCurrentTest().start();
    }

    private List<List<ComponentTest>> testGroups = new ArrayList<>();

    /**
     * Register a new test group
     *
     * @param group the test group to register
     */
    public void registerTestGroup(List<ComponentTest> group) {
        testGroups.add(group);
    }
}
