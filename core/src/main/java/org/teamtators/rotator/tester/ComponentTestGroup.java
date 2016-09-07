package org.teamtators.rotator.tester;

import java.util.Arrays;
import java.util.List;

public class ComponentTestGroup {
    private List<ComponentTest> tests;
    private String name;

    public ComponentTestGroup(String name, ComponentTest... tests) {
        this(name, Arrays.asList(tests));
    }

    public ComponentTestGroup(String name, List<ComponentTest> tests) {
        this.name = name;
        this.tests = tests;
    }

    public String getName() {
        return name;
    }

    public void addTest(ComponentTest test) {
        tests.add(test);
    }

    public List<ComponentTest> getTests() {
        return tests;
    }
}
