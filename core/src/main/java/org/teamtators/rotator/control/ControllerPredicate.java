package org.teamtators.rotator.control;

public interface ControllerPredicate {
    boolean compute(double delta, AbstractController controller);
}
