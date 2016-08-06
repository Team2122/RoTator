package org.teamtators.rotator.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.wpi.first.wpilibj.VictorSP;

import java.io.IOException;

@JsonDeserialize(using = VictorSPConfig.Deserializer.class)
public class VictorSPConfig  {

    public static class Deserializer extends JsonDeserializer<VictorSPConfig> {
        @Override
        public VictorSPConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.isInt()) {
                VictorSPConfig config = new VictorSPConfig();
                config.setChannel(node.asInt());
                return config;
            } else if (node.isObject()) {
                return new ObjectMapper().treeToValue(node, VictorSPConfig.class);
            } else {
                throw new JsonParseException(p, "VictorSP config must be an int or an object");
            }
        }
    }

    private int channel;
    private boolean inverted = false;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public VictorSP create() {
        VictorSP victorSP = new VictorSP(channel);
        victorSP.setInverted(inverted);
        return victorSP;
    }
}
