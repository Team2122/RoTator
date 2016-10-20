package org.teamtators.rotator.scheduler;

public class TriggerAdder {
    TriggerSource triggerSource;
    private Scheduler scheduler;

    TriggerAdder(Scheduler scheduler, TriggerSource triggerSource) {
        this.scheduler = scheduler;
        this.triggerSource = triggerSource;
    }

    public TriggerBinder run(Runnable runnable) {
        return new TriggerBinder(runnable);
    }

    public TriggerBinder start(Command command) {
        return new TriggerBinder(() -> scheduler.startCommand(command));
    }

    public TriggerBinder cancel(Command command) {
        return new TriggerBinder(() -> scheduler.cancelCommand(command));
    }

    public TriggerBinder toggle(Command command) {
        return new TriggerBinder(() -> {
            if (command.isRunning()) {
                scheduler.cancelCommand(command);
            } else {
                scheduler.startCommand(command);
            }
        });
    }

    public void whilePressed(Command command) {
        scheduler.addTrigger(triggerSource, new WhilePressedScheduler(command));
    }

    public class TriggerBinder {
        Runnable runnable;

        public TriggerBinder(Runnable runnable) {
            this.runnable = runnable;
        }

        private void putScheduler(TriggerScheduler triggerScheduler) {
            scheduler.addTrigger(triggerSource, triggerScheduler);
        }

        public TriggerAdder whenPressed() {
            putScheduler(TriggerSchedulers.onActive(runnable));
            return TriggerAdder.this;
        }

        public TriggerAdder whenReleased() {
            putScheduler(TriggerSchedulers.onInactive(runnable));
            return TriggerAdder.this;
        }

        public TriggerAdder whilePressed() {
            putScheduler(TriggerSchedulers.whileActive(runnable));
            return TriggerAdder.this;
        }

        public TriggerAdder whileReleased() {
            putScheduler(TriggerSchedulers.whileInactive(runnable));
            return TriggerAdder.this;
        }
    }

    private class WhilePressedScheduler implements TriggerScheduler {
        private final Command command;
        private boolean iStarted = false;

        public WhilePressedScheduler(Command command) {
            this.command = command;
        }

        @Override
        public void processTrigger(boolean active) {
            boolean running = command.isRunning();
            if (active && !running) {
                scheduler.startCommand(command);
                iStarted = true;
            } else if (!active && running && iStarted) {
                scheduler.cancelCommand(command);
                iStarted = false;
            }
        }
    }
}
