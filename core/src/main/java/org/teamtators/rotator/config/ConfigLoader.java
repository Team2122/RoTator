package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private String configDir;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    public ConfigLoader(@Named("configDir") String configDir) {
        this.configDir = configDir;
    }

    /**
     * Get a config, modified for the specified profile
     *
     * @param configName  Config to get
     * @param profileName Profile to apply
     * @return Combined config for profile
     */
    public JsonNode getProfileConfig(String configName, String profileName) {
        JsonNode mainConfig = load(configName);
        if (profileName.equals("empty")) {
            return mainConfig;
        }
        File file = new File(configDir + File.separator + profileName + File.separator + configName);
        File folder = new File(configDir + File.separator + profileName);
        // check if we should just use default config
        if (!file.exists()) {
            logger.info("No profile config file exists for {}, using base config", configName);
            return mainConfig;
        } else if (!folder.exists()) {
            logger.warn("No such profile as {} found, base config will be used.", profileName);
            return mainConfig;
        } else {
            JsonNode profileConfig = load(profileName + File.separator + configName);
            return mergeNodes(mainConfig, profileConfig);
        }
    }

    private JsonNode load(String fileName) {
        String filePath = configDir + File.separator + fileName;
        try (InputStream fileStream = new FileInputStream(filePath)) {
            logger.debug("Loading config from path {}", filePath);
            return objectMapper.reader().readTree(fileStream);
        } catch (IOException e) {
            throw new ConfigException(String.format("Error loading config %s", fileName), e);
        }
    }

    /**
     * Combine two JsonNodes
     *
     * @param mainNode   Node to merge into
     * @param updateNode Node to merge with
     * @return Combined node
     */
    private JsonNode mergeNodes(JsonNode mainNode, JsonNode updateNode) {
        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode jsonNode = mainNode.get(fieldName);
            // if field exists and is an embedded object
            if (jsonNode != null && jsonNode.isObject()) {
                // recurse into embedded object
                mergeNodes(jsonNode, updateNode.get(fieldName));
            } else {
                if (mainNode instanceof ObjectNode) {
                    // overwrite field
                    JsonNode value = updateNode.get(fieldName);
                    ((ObjectNode) mainNode).replace(fieldName, value);
                }
            }
        }
        return mainNode;
    }
}
