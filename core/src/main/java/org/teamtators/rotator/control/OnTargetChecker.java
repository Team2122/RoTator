package org.teamtators.rotator.control;

public interface OnTargetChecker {
    boolean compute(double delta, AbstractController controller);
}
