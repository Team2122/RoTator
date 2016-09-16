package org.teamtators.rotator.control;

public interface LimitPredicate {
    LimitState getLimit(AbstractController controller);
}
