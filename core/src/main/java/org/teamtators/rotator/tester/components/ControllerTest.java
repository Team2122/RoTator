package org.teamtators.rotator.tester.components;

import org.teamtators.rotator.control.AbstractController;
import org.teamtators.rotator.tester.ComponentTest;

public class ControllerTest extends ComponentTest {
    private AbstractController controller;

    public ControllerTest(String name, AbstractController controller) {
        super(name);
        this.controller = controller;
    }


}
