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
        ComponentTest test = getCurrentTest();
        if(test != null) {
            test.step();
            test.updateAxis(LogitechF310.Axis.RIGHT_STICK_Y,
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
                            test.onButtonDown(button);
                        }
                    } else if (lastValue && !value) {
                        test.onButtonUp(button);
                    }
                }
                lastStates.put(button, value);
            }
        }
        return false;
    }

    private ComponentTest getCurrentTest() {
        TestGroup group = getCurrentTestGroup();
        if(group != null) {
            return group.getTest(testIndex);
        }
        else {
            return null;
        }
    }

    private TestGroup getCurrentTestGroup() {
        return testGroups.get(testGroupIndex);
    }

    @Override
    protected void finish(boolean interrupted) {
        getCurrentTest().stop();
    }

    private void stopTest() {
        ComponentTest test = getCurrentTest();
        if(test != null) {
            test.stop();
        }
    }

    public void nextTest() {
        stopTest();
        testIndex++;
        if (testIndex >= getCurrentTestGroup().count()) {
            testIndex = 0;
        }
        startNextTest();
    }

    public void previousTest() {
        stopTest();
        testIndex--;
        if (testIndex < 0) {
            testIndex = getCurrentTestGroup().count() - 1;
        }
        startNextTest();
    }

    public void nextTestGroup() {
        stopTest();
        testGroupIndex++;
        if (testGroupIndex >= testGroups.size()) {
            testGroupIndex = 0;
        }
        startNextTest();
    }

    public void previousTestGroup() {
        stopTest();
        testGroupIndex--;
        if (testGroupIndex < 0) {
            testGroupIndex = testGroups.size() - 1;
        }
        startNextTest();
    }

    private void startNextTest() {
        ComponentTest test = getCurrentTest();
        if(test != null) {
            logger.info("Starting {}", test.getName());
            test.start();
        }
        else {
            logger.warn("Test group is empty!");
        }
    }

    private List<TestGroup> testGroups = new ArrayList<>();

    /**
     * Register a new test group
     *
     * @param group the test group to register
     */
    public void registerTestGroup(TestGroup group) {
        testGroups.add(group);
    }
}
