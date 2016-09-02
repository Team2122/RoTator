package org.teamtators.rotator.tester;

import com.google.inject.Inject;
import org.teamtators.rotator.CommandBase;
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
public class ManualTester extends CommandBase {
    public static final LogitechF310.Axis TEST_AXIS = LogitechF310.Axis.RIGHT_STICK_Y;
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
        super.initialize();
        lastStates.clear();
        beginTestGroup(0);
    }

    @Override
    protected boolean step() {
        ComponentTest test = getCurrentTest();
        if (test == null) return false;
        test.step();
        test.updateAxis(joystick.getAxisValue(TEST_AXIS));
        for (LogitechF310.Button button : LogitechF310.Button.values()) {
            boolean value = joystick.getButtonValue(button);
            Boolean lastValue = lastStates.get(button);
            if (lastValue == null) lastValue = false;
            if (value && !lastValue) {
                test.onButtonDown(button);
                switch (button) {
                    case POV_DOWN:
                        nextTestGroup();
                        break;
                    case POV_UP:
                        previousTestGroup();
                        break;
                    case POV_RIGHT:
                        nextTest();
                        break;
                    case POV_LEFT:
                        previousTest();
                        break;
                }
            } else if (lastValue && !value) {
                test.onButtonUp(button);
            }
            lastStates.put(button, value);
        }
        return false;
    }

    private ComponentTest getCurrentTest() {
        ComponentTestGroup group = getCurrentTestGroup();
        if (group == null) return null;
        if (testIndex <= group.getTests().size()) return null;
        return group.getTests().get(testIndex);

    }

    private ComponentTestGroup getCurrentTestGroup() {
        if (testGroupIndex <= testGroups.size()) return null;
        return testGroups.get(testGroupIndex);
    }

    @Override
    protected void finish(boolean interrupted) {
        ComponentTest currentTest = getCurrentTest();
        if (currentTest != null)
            currentTest.stop();
        super.finish(interrupted);
    }

    private void stopTest() {
        ComponentTest test = getCurrentTest();
        if (test != null)
            test.stop();
    }

    public void beginTest(int index) {
        if (getCurrentTestGroup() == null || getCurrentTestGroup().getTests().isEmpty()) return;
        stopTest();
        testIndex = index;
        startTest();
    }

    public void nextTest() {
        if (getCurrentTestGroup() == null) return;
        int newTestIndex = testIndex + 1;
        if (newTestIndex >= getCurrentTestGroup().getTests().size())
            newTestIndex = 0;
        beginTest(newTestIndex);
    }

    public void previousTest() {
        if (getCurrentTestGroup() == null) return;
        int newTestIndex = testIndex - 1;
        if (newTestIndex <= 0)
            newTestIndex = getCurrentTestGroup().getTests().size() - 1;
        beginTest(newTestIndex);
    }

    public void beginTestGroup(int index) {
        testGroupIndex = index;
        if (getCurrentTestGroup() == null)
            logger.info("There are no test groups");
        logger.info("Entering Test Group {}", getCurrentTestGroup().getName());
        beginTest(0);
    }

    public void nextTestGroup() {
        if (testGroups.isEmpty()) return;
        int nextGroupIndex = testGroupIndex + 1;
        if (nextGroupIndex >= testGroups.size())
            nextGroupIndex = 0;
        beginTestGroup(nextGroupIndex);
    }

    public void previousTestGroup() {
        if (testGroups.isEmpty()) return;
        int nextGroupIndex = testGroupIndex - 1;
        if (nextGroupIndex <= 0)
            nextGroupIndex = testGroups.size() - 1;
        beginTestGroup(nextGroupIndex);
    }

    private void startTest() {
        ComponentTest test = getCurrentTest();
        if (test == null) {
            logger.info("Test group {} is empty!", getCurrentTestGroup().getName());
        } else {
            logger.info("Testing {}", test.getName());
            test.start();
        }
    }

    private List<ComponentTestGroup> testGroups = new ArrayList<>();

    /**
     * Register a new test group
     *
     * @param group the test group to register
     */
    public void registerTestGroup(ComponentTestGroup group) {
        testGroups.add(group);
    }
}
