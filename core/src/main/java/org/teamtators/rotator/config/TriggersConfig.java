package org.teamtators.rotator.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Multimap;
import org.teamtators.rotator.operatorInterface.LogitechF310;
import org.teamtators.rotator.scheduler.RobotState;

import java.util.Map;
import java.util.Set;

public class TriggersConfig {
    @JsonProperty("Driver")
    public Map<LogitechF310.Button, JsonNode> driver;
    @JsonProperty("Gunner")
    public Map<LogitechF310.Button, JsonNode> gunner;
    public Set<String> defaults;
    public Multimap<RobotState, String> stateCommands;
}
