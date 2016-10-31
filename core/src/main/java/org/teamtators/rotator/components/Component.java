package org.teamtators.rotator.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Component {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String name;

    public Component(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
