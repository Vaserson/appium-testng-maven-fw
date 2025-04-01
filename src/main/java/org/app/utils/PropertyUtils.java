package org.app.utils;

import org.app.exceptions.PropertyFileUsageException;
import org.app.exceptions.XmlFileUsageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.io.IOException;

public final class PropertyUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtils.class);

    private static final Properties properties = new Properties();
    private static final Map<String, String> CONFIGMAP;

    private PropertyUtils() {}

    static {
        CONFIGMAP = loadProperties();
    }

    private static Map<String, String> loadProperties() {
        return loadProperties(getConfigFilePath());
    }

    public static Map<String, String> loadProperties(String propertyFilePath) {
        Map<String, String> configMap = new HashMap<>();

        if (propertyFilePath == null) {
            LOGGER.warn("Skipping property loading as no file is provided.");
            return configMap;
        }

        try (InputStream inputStream = PropertyUtils.class.getClassLoader().getResourceAsStream(propertyFilePath)) {
            if (inputStream == null) {
                LOGGER.error("Property file not found: {}", propertyFilePath);
                throw new PropertyFileUsageException("Property file not found: " + propertyFilePath);
            }

            properties.load(inputStream);
            for (String key : properties.stringPropertyNames()) {
                configMap.put(key, properties.getProperty(key));
            }
            LOGGER.info("Properties loaded successfully from: {}", propertyFilePath);
        } catch (IOException e) {
            LOGGER.error("Error loading properties from file: {}", e.getMessage());
            throw new PropertyFileUsageException("Failed to load properties from file: " + propertyFilePath, e);
        }
        return configMap;
    }

    private static String getConfigFilePath() {
        String configFile = System.getProperty("config.file");
        if (configFile == null || configFile.isEmpty()) {
            LOGGER.warn("No config file specified, returning null");
            return null;
        }
        LOGGER.info("Using config file: {}", configFile);
        return configFile;
    }

    /**
     * Parses an XML file and extracts string elements into a map.
     *
     * @param file InputStream of the XML file.
     * @return Map containing name-value pairs from the XML.
     * @throws XmlFileUsageException if parsing fails.
     */
    public static Map<String, String> parseStringXML(InputStream file) {
        if (file == null) {
            throw new XmlFileUsageException("Input stream for XML file is null.");
        }

        Map<String, String> stringMap = new HashMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList nList = document.getElementsByTagName("string");
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    String name = eElement.getAttribute("name");
                    String value = eElement.getTextContent();
                    if (name.isEmpty() || value.isEmpty()) {
                        LOGGER.warn("Skipping empty or invalid string entry in XML: name='{}', value='{}'", name, value);
                        continue;
                    }
                    if (stringMap.containsKey(name)) {
                        LOGGER.warn("Duplicate key '{}' found in XML. Overwriting with new value '{}'.", name, value);
                    }
                    stringMap.put(name, value);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error parsing XML file: {}", e.getMessage());
            throw new XmlFileUsageException("Failed to parse XML file", e);
        }
        return stringMap;
    }

    /**
     * Retrieves a property value by its key, with caching.
     *
     * @param key The property key.
     * @return The property value.
     * @throws PropertyFileUsageException if the property is not found or empty.
     */
    public static String getProperty(String key) {
        return CONFIGMAP.computeIfAbsent(key, k -> {
            String value = System.getProperty(k);
            if (value == null) {
                LOGGER.warn("Key '{}' not found in system properties, checking config file", k);
                value = properties.getProperty(k);
                if (value == null || value.isEmpty()) {
                    LOGGER.error("Property '{}' not found or empty. Check configuration.", k);
                    throw new PropertyFileUsageException("Property '" + k + "' not found or empty. Check configuration.");
                }
            }
            return value;
        });
    }

    public static Map<String, String> loadStrings() {
        String stringsXmlPath = getProperty("stringsXml");
        if (stringsXmlPath == null || stringsXmlPath.isEmpty()) {
            LOGGER.error("Strings XML path not found in properties");
            return new HashMap<>();
        }

        try (InputStream inputStream = PropertyUtils.class.getClassLoader().getResourceAsStream(stringsXmlPath)) {
            if (inputStream == null) {
                LOGGER.error("Strings XML file not found: {}", stringsXmlPath);
                return new HashMap<>();
            }

            Map<String, String> strings = new HashMap<>();
            // Add your XML parsing logic here
            return strings;
        } catch (IOException e) {
            LOGGER.error("Error loading strings from XML: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
