package org.apidemos.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public final class PropertyUtils {
    private static final Logger logger = LogManager.getLogger(PropertyUtils.class);

    private static Properties property = new Properties();
    private static final Map<String, String> CONFIGMAP = new HashMap<>();


    private PropertyUtils(){}

    static {
        try {
            FileInputStream file = new FileInputStream("src/test/resources/apiDemos.properties");
            property.load(file);
            for(Map.Entry<Object, Object> entry : property.entrySet()) {
                CONFIGMAP.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> parseStringXML(InputStream file) throws Exception {
        HashMap<String, String> stringMap = new HashMap<>();

        // Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Build Document
        Document document = builder.parse(file);

        // Normalize the XML Structure. TOO IMPORTANT!
        document.getDocumentElement().normalize();

/*        // Root node
        Element root = document.getDocumentElement(); // resources from xml
//        System.out.println(root.getNodeName());*/

        // Get all elements
        NodeList nList = document.getElementsByTagName("string");
        System.out.println("=====================================");

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            System.out.println(); // separator
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                // Store each element key value in map
                stringMap.put(eElement.getAttribute("name"), eElement.getTextContent());
            }
        }
        return stringMap;
    }

    public static String getProperty(String key) {
        logger.info("Searching system property '{}'", key);
        String property = System.getProperty(key);
        if (Objects.isNull(property)) {
            logger.info("No system property for '{}' is found.", key);
            return getConfigProperty(key);
        } else {
            logger.info("System property for '{}' is: {}].", key, property);
            return property;
        }
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