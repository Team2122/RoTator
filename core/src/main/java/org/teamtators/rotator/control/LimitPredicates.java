package org.teamtators.rotator.control;

public class LimitPredicates {
    public static LimitPredicate nevetAtLimit() {
        return controller -> LimitState.NEITHER;
    }
}
