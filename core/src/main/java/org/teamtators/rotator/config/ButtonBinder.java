package org.teamtators.rotator.config;

import com.google.inject.Inject;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.Command;
import org.teamtators.rotator.scheduler.CommandStore;
import org.teamtators.rotator.scheduler.Scheduler;
import org.teamtators.rotator.scheduler.TriggerAdder;

import java.util.Iterator;
import java.util.Map;

public class ButtonBinder {
    Scheduler scheduler;
    CommandStore commandStore;

    @Inject
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Inject
    public void setCommandStore(CommandStore commandStore) {
        this.commandStore = commandStore;
    }

    public void bindButtonsToLogitechF310(Map<LogitechF310.Button, String> buttonsMap, LogitechF310 joystick) {
        Iterator it = buttonsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry line = (Map.Entry) it.next();
            TriggerAdder onTrigger = scheduler.onTrigger(joystick.getTriggerSource((LogitechF310.Button) line.getKey()));
            String bindingSpecifier = (String) line.getValue();
            int spaceIndex = bindingSpecifier.indexOf(" ");
            String bindingType = bindingSpecifier.substring(0, spaceIndex);
            String commandString = bindingSpecifier.substring(spaceIndex + 1, bindingSpecifier.length());
            Command command = commandStore.getCommand(commandString);
            switch(bindingType) {
                case "WhenPressed":
                    onTrigger.start(command).whenPressed();
                    break;
                case "WhenReleased":
                    onTrigger.start(command).whenReleased();
                    break;
                case "ToggleWhenPressed":
                    onTrigger.toggle(command).whenPressed();
                    break;
                case "ToggleWhenReleased":
                    onTrigger.toggle(command).whenReleased();
                    break;
                case "WhilePressed":
                    onTrigger.whilePressed(command);
                    break;
                case "WhileReleased":
                    onTrigger.whileReleased(command);
                    break;
                default:
                    throw new ConfigException("Specified binding type " + bindingType + " is invalid.");
            }
        }
    }
}
