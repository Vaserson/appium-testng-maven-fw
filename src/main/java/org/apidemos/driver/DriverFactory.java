package org.apidemos.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apidemos.appium.AppiumServerManager;
import org.apidemos.utils.PropertyUtils;

import java.net.URI;
import java.net.URL;

public class DriverFactory {
    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

    private static AppiumDriver driver;

    private static final String APP_PACKAGE = PropertyUtils.getProperty("androidAppPackage");
    private static final String APP_ACTIVITY = PropertyUtils.getProperty("androidAppActivity");


    private static String getAppiumURL() {
        String appiumURL = AppiumServerManager.getAppiumServiceUrl();

        if (appiumURL == null || appiumURL.isEmpty()) {
            String defaultAppiumURL = "http://127.0.0.1:4723";
            LOGGER.warn("Appium service URL not found in configuration. Using default Appium URL: [{}]", defaultAppiumURL);
            return defaultAppiumURL;
        }

        LOGGER.info("Appium service URL retrieved successfully: [{}]", appiumURL);
        return appiumURL;
    }

    public static AppiumDriver getDriver() {
        if (driver == null) {
            try {
                String appiumURL = getAppiumURL();
                URL url = new URI(appiumURL).toURL();

                UiAutomator2Options options = new UiAutomator2Options()
                        .setPlatformName("Android")
                        .setAppPackage(APP_PACKAGE)
                        .setAppActivity(APP_ACTIVITY)
                        .noReset();

                driver = new AndroidDriver(url, options);
                driver.setSetting("imageMatchThreshold", "0.6");

                LOGGER.info("Appium Driver initialized successfully.");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Appium Driver. Error: {}", e.getMessage());
                throw new RuntimeException("Failed to initialize Appium Driver", e);
            }
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            try {
                LOGGER.info("Attempting to quit driver...");
                driver.quit();
                LOGGER.info("Driver quit successfully.");
            } catch (Exception e) {
                LOGGER.error("Error while quitting driver: {}", e.getMessage(), e);
            } finally {
                driver = null;
                LOGGER.debug("Driver instance set to null.");
            }
        } else {
            LOGGER.warn("Attempted to quit driver, but driver was already null.");
        }
    }
}