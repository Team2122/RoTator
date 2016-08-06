package org.teamtators.rotator.scheduler;

import java.util.*;

public class TriggerProcessor extends Command {
    private Map<TriggerSource, List<TriggerScheduler>> triggerSchedulers = new HashMap<>();

    public TriggerProcessor() {
        super("TriggerProcessor");
    }

    public void putScheduler(TriggerSource source, TriggerScheduler scheduler) {
        List<TriggerScheduler> schedulers = triggerSchedulers.get(source);
        if (schedulers == null) {
            triggerSchedulers.put(source, new ArrayList<>(Collections.singletonList(scheduler)));
        } else {
            schedulers.add(scheduler);
        }
    }

    public TriggerAdder on(TriggerSource triggerSource) {
        return new TriggerAdder(triggerSource);
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected boolean step() {
        for (Map.Entry<TriggerSource, List<TriggerScheduler>> entry : triggerSchedulers.entrySet()) {
            TriggerSource triggerSource = entry.getKey();
            boolean active = triggerSource.getActive();
            for (TriggerScheduler scheduler : entry.getValue()) {
                scheduler.processTrigger(active);
            }
        }
        return false;
    }

    @Override
    protected void finish(boolean interrupted) {

    }

    public class TriggerAdder {
        TriggerSource triggerSource;

        TriggerAdder(TriggerSource triggerSource) {
            this.triggerSource = triggerSource;
        }

        public TriggerBinder run(Runnable runnable) {
            return new TriggerBinder(runnable);
        }

        public TriggerBinder start(Command command) {
            return new TriggerBinder(() -> startCommand(command));
        }

        public TriggerBinder cancel(Command command) {
            return new TriggerBinder(() -> cancelCommand(command));
        }

        public TriggerBinder toggle(Command command) {
            return new TriggerBinder(() -> {
                if (command.isRunning()) {
                    cancelCommand(command);
                } else {
                    startCommand(command);
                }
            });
        }

        public class TriggerBinder {
            Runnable runnable;

            public TriggerBinder(Runnable runnable) {
                this.runnable = runnable;
            }

            private void putScheduler(TriggerScheduler scheduler) {
                TriggerProcessor.this.putScheduler(triggerSource, scheduler);
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
    }
}
