package org.teamtators.rotator.datastream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import org.teamtators.rotator.control.Steppable;
import org.teamtators.rotator.scheduler.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class DataCollector implements Steppable {
    private Scheduler scheduler;
    private DataServer dataServer;
    private List<Subsystem> subsystems;

    @Inject
    public DataCollector() {

    }

    @Override
    public void step(double delta) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("graphvalue", (System.currentTimeMillis() / 1000) % 10);
        List<Object> commandsData = new ArrayList<>();
        Map<String,ICommandRun> commands = scheduler.getRunningCommands();
        for(String key : commands.keySet()) {
            ICommandRun commandRun = commands.get(key);
            Command command = commandRun.getCommand();
            Map<String,Object> commandMap = new HashMap<>();
            commandMap.put("name", key);
            if(command instanceof DataProvider) {
                ((DataProvider) command).addData(commandMap);
            }
            commandsData.add(commandMap);
        }
        map.put("commands", commandsData);
        Map<String, Object> subsystemData = new HashMap<>();
        for(Subsystem subsystem : subsystems) {
            Map<String, Object> subsystemMap = new HashMap<>();
            if(subsystem instanceof DataProvider) {
                ((DataProvider) subsystem).addData(subsystemData);
            }
            subsystemData.put(subsystem.getName(), subsystemMap);
        }
        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        String data = null;
        try {
            data = mapper.writeValueAsString(map);
            dataServer.setData(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Inject
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Inject
    public void setDataServer(DataServer dataServer) {
        this.dataServer = dataServer;
    }

    @Inject
    public void setSubsystems(List<Subsystem> subsystems) {
        this.subsystems = subsystems;
    }
}
