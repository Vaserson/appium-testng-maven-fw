package org.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.exceptions.PropertyFileUsageException;
import org.app.exceptions.XmlFileUsageException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public final class PropertyUtils {
    private static final Logger LOGGER = LogManager.getLogger(PropertyUtils.class);

    private static final Properties properties = new Properties();
    private static final Map<String, String> CONFIGMAP;
    private static final Map<String, String> CACHE = new HashMap<>();

    private PropertyUtils() {}

    static {
        CONFIGMAP = loadProperties();
    }

    private static Map<String, String> loadProperties() {
        String propertyFilePath = getConfigFilePath();
        Map<String, String> tempConfigMap = new HashMap<>();
        try (FileInputStream file = new FileInputStream(propertyFilePath)) {
            properties.load(file);
            for (String key : properties.stringPropertyNames()) {
                tempConfigMap.put(key, properties.getProperty(key));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load properties file from path: [{}] with message [{}]", propertyFilePath, e.getMessage());
            throw new PropertyFileUsageException("Unable to load configuration properties", e);
        }
        return Collections.unmodifiableMap(tempConfigMap);
    }

    //TODO Remove hardcoding, implement *.properties file per application
    private static String getConfigFilePath() {
        return System.getProperty("config.file", "src/test/resources/apiDemos.properties");
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
        if (key == null || key.isEmpty()) {
            throw new PropertyFileUsageException("Property key cannot be null or empty.");
        }

        return CACHE.computeIfAbsent(key, k -> {
            LOGGER.debug("Fetching property for key '{}'", k);
            String value = System.getProperty(k);
            if (Objects.nonNull(value)) {
                LOGGER.debug("Key '{}' found in system properties with value '{}'", k, value);
            } else {
                LOGGER.warn("Key '{}' not found in system properties, checking config file", k);
                value = CONFIGMAP.get(k);
            }
            if (Objects.nonNull(value)) {
                LOGGER.debug("Key '{}' found in config properties with value '{}'", k, value);
            } else {
                throw new PropertyFileUsageException("Property '" + k + "' not found or empty. Check configuration.");
            }
            return value;
        });
    }
}
