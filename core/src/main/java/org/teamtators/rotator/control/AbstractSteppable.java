package org.teamtators.rotator.control;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractSteppable implements Steppable {
    private Stepper stepper;

    public final Stepper getStepper() {
        return stepper;
    }

    @Inject
    public final void setStepper(@ForController Stepper stepper) {
        this.stepper = stepper;
    }

    @Override
    public abstract void step(double delta);

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    public final void enable() {
        checkNotNull(stepper, "A controller must be assigned a stepper before being enabled");
        stepper.add(this);
    }

    public final void disable() {
        if (stepper != null)
            stepper.remove(this);
    }

    public final boolean isEnabled() {
        return stepper != null && stepper.contains(this);
    }
}
