package org.teamtators.rotator.tester;

import java.util.ArrayList;
import java.util.List;

public class TestGroup {
    private List<ComponentTest> tests;
    private String name;

    public TestGroup(String name) {
        this.name = name;
        tests = new ArrayList<>();
    }

    public void addTest(ComponentTest test) {
        tests.add(test);
    }

    public int count() {
        return tests.size();
    }

    public ComponentTest getTest(int index) {
        return tests.get(index);
    }

    public String getName() {
        return name;
    }
}
