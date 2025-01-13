package org.apidemos.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public final class PropertyUtils {
    private static final Logger logger = LogManager.getLogger(PropertyUtils.class);

    private static final Properties property = new Properties();
    private static final Map<String, String> CONFIGMAP = new HashMap<>();
    private static final Map<String, String> CACHE = new HashMap<>();

    private PropertyUtils() {}

    static {
        try {
            FileInputStream file = new FileInputStream("src/test/resources/apiDemos.properties");
            property.load(file);
            for (Map.Entry<Object, Object> entry : property.entrySet()) {
                CONFIGMAP.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        } catch (IOException e) {
            logger.error("Failed to load properties file", e);
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> parseStringXML(InputStream file) throws Exception {
        HashMap<String, String> stringMap = new HashMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();

        NodeList nList = document.getElementsByTagName("string");
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                stringMap.put(eElement.getAttribute("name"), eElement.getTextContent());
            }
        }
        return stringMap;
    }

    public static String getProperty(String key) {
        if (CACHE.containsKey(key)) {
            logger.debug("Returning cached value for '{}': {}", key, CACHE.get(key));
            return CACHE.get(key);
        }

        logger.info("Searching system property '{}'", key);
        String property = System.getProperty(key);

        if (Objects.isNull(property)) {
            logger.info("No system property for '{}' is found.", key);
            property = getConfigProperty(key);
        } else {
            logger.info("System property for '{}' is: {}", key, property);
        }

        CACHE.put(key, property); // Кешируем результат
        return property;
    }

    public static String getConfigProperty(String key) {
        logger.info("Searching property '{}' inside properties file", key);
        String property = CONFIGMAP.get(key);
        if (Objects.nonNull(property)) {
            logger.info("Property for '{}' is: {}.", key, property);
            return property;
        } else {
            throw new RuntimeException("No '" + key + "' property found. Please check the documentation for instructions to setting up testing data");
        }
    }
}