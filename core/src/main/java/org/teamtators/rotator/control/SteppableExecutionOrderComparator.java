package org.teamtators.rotator.control;

import java.util.Comparator;

class SteppableExecutionOrderComparator implements Comparator<Steppable> {
    @Override
    public int compare(Steppable o1, Steppable o2) {
        return o1.getExecutionOrder() - o2.getExecutionOrder();
    }
}
