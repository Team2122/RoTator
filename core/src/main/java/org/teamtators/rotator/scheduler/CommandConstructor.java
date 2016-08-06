package org.teamtators.rotator.scheduler;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface CommandConstructor {
    Command constructCommand() throws InvocationTargetException;
}
