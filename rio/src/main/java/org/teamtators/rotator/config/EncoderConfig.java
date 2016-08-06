package org.teamtators.rotator.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.wpi.first.wpilibj.Encoder;

import java.io.IOException;

@JsonDeserialize(using = EncoderConfig.Deserializer.class)
public class EncoderConfig {
    public static class Deserializer extends JsonDeserializer<EncoderConfig> {
        @Override
        public EncoderConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.isArray() && node.size() != 2) {
                EncoderConfig encoderConfig = new EncoderConfig();
                ArrayNode array = (ArrayNode) node;
                encoderConfig.setaChannel(array.get(0).asInt());
                encoderConfig.setbChannel(array.get(1).asInt());
                return encoderConfig;
            } else if (node.isObject()) {
                return new ObjectMapper().treeToValue(node, EncoderConfig.class);
            } else {
                throw new JsonParseException(p, "Encoder config must contain an array of 2 channels or an object");
            }
        }
    }

    private int aChannel, bChannel;
    private boolean reverse = false;

    public int getaChannel() {
        return aChannel;
    }

    private void setaChannel(int aChannel) {
        this.aChannel = aChannel;
    }

    public int getbChannel() {
        return bChannel;
    }

    private void setbChannel(int bChannel) {
        this.bChannel = bChannel;
    }

    public boolean isReverse() {
        return reverse;
    }

    private void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public Encoder create() {
        return new Encoder(aChannel, bChannel, reverse);
    }
}
