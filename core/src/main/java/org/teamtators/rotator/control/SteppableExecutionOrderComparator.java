package org.teamtators.rotator.control;

import java.util.Comparator;

class SteppableExecutionOrderComparator implements Comparator<Steppable> {
    @Override
    public int compare(Steppable o1, Steppable o2) {
        int diff = o1.getExecutionOrder() - o2.getExecutionOrder();
        if (diff == 0) {
            return System.identityHashCode(o1) - System.identityHashCode(o2);
        }
        return diff;
    }
}
