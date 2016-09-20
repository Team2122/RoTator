package org.teamtators.rotator.control;

import java.util.function.BooleanSupplier;

public class LimitPredicates {
    public static LimitPredicate neverAtLimit() {
        return controller -> LimitState.NEITHER;
    }

    public static LimitPredicate doubleLimits(BooleanSupplier negativeLimit, BooleanSupplier positiveLimit) {
        return new DoubleLimitPredicate(negativeLimit, positiveLimit);
    }

    public static class DoubleLimitPredicate implements LimitPredicate {
        private BooleanSupplier negativeLimit;
        private BooleanSupplier positiveLimit;

        public DoubleLimitPredicate(BooleanSupplier negativeLimit, BooleanSupplier positiveLimit) {
            this.negativeLimit = negativeLimit;
            this.positiveLimit = positiveLimit;
        }

        public BooleanSupplier getNegativeLimit() {
            return negativeLimit;
        }

        public BooleanSupplier getPositiveLimit() {
            return positiveLimit;
        }

        @Override
        public LimitState getLimit(AbstractController controller) {
            if (negativeLimit.getAsBoolean()) return LimitState.NEGATIVE;
            else if (positiveLimit.getAsBoolean()) return LimitState.POSITIVE;
            else return LimitState.NEITHER;
        }
    }
}
