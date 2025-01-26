package org.app.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocatorUtils {
    private static final Logger LOGGER = LogManager.getLogger(LocatorUtils.class);

    private static final Map<String, Map<String, Map<String, String>>> LOCATORS = new HashMap<>();

    // Static initializer to load all locators at startup
    static {
        loadLocators();
    }

    private static void loadLocators() {
        String LOCATORS_PATH = PropertyUtils.getProperty("locators.path");

        ObjectMapper mapper = new ObjectMapper();
        File folder = new File(LOCATORS_PATH);

        if (!folder.exists() || !folder.isDirectory()) {
            LOGGER.error("Locators directory not found: {}", LOCATORS_PATH);
            throw new RuntimeException("Locators directory not found: " + LOCATORS_PATH);
        }

        for (File file : Objects.requireNonNull(folder.listFiles((dir, name) -> name.endsWith(".json")))) {
            try {
                Map<String, Map<String, Map<String, String>>> fileContent = mapper.readValue(
                        file, new TypeReference< >() {
                        }
                );
                String fileName = file.getName().replace(".json", "");
                LOCATORS.put(fileName, fileContent.get("locators"));
            } catch (Exception e) {
                LOGGER.error("Failed to load locators from file: {}\n{}", file.getName(), e.getMessage());
                throw new RuntimeException("Failed to load locators from file: " + file.getName(), e);
            }
        }
    }

    /**
     * Fetches a locator by key from all loaded locators.
     *
     * @param key the locator key.
     * @return the map of locator details (e.g., xpath, id).
     */
    public static Map<String, String> getLocator(String key) {
        for (Map.Entry<String, Map<String, Map<String, String>>> fileEntry : LOCATORS.entrySet()) {
            Map<String, Map<String, String>> fileLocators = fileEntry.getValue();
            if (fileLocators.containsKey(key)) {
                Map<String, String> foundLocator = fileLocators.get(key);
                LOGGER.error("Locator for key [{}] is: {}", key, foundLocator);
                return foundLocator;
            }
        }
        LOGGER.error("Locator not found: {}", key);
        throw new RuntimeException("Locator not found: " + key);
    }
}