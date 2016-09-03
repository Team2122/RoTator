package org.teamtators.rotator.control;

public interface ITargetChecker {
    boolean compute(double delta, AbstractController controller);
}
