package org.teamtators.rotator.tester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestGroup {
    private List<ComponentTest> tests;
    private String name;

    public TestGroup(String name, Collection<ComponentTest> tests) {
        this.name = name;
        this.tests = new ArrayList<>(tests);
    }

    public String getName() {
        return name;
    }

    public List<ComponentTest> getTests() {
        return tests;
    }
}
