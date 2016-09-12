package org.teamtators.rotator.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private String configDir;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    public ConfigLoader(@Named("configDir") String configDir) {
        this.configDir = configDir;
    }

    public JsonNode load(String fileName) {
        String filePath = configDir + File.separator + fileName;
        try (InputStream fileStream = new FileInputStream(filePath)) {
            logger.debug("Loading config from path {}", filePath);
            return objectMapper.reader().readTree(fileStream);
        } catch (IOException e) {
            throw new ConfigException(String.format("Error loading config %s", fileName), e);
        }
    }
}
