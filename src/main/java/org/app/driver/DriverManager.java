package org.app.driver;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverManager.class);
    private static final Map<String, WebDriver> webDrivers = new HashMap<>();
    private static final Map<String, AppiumDriver> mobileDrivers = new HashMap<>();

    private DriverManager() {}

    public static AppiumDriver initMobileDriver(String key) {
        try {
            if (!mobileDrivers.containsKey(key)) {
                LOGGER.info("Initializing new mobile driver with key: {}", key);
                AppiumDriver driver = DriverFactory.createMobileDriver(key);
                mobileDrivers.put(key, driver);
                return driver;
            }
            LOGGER.info("Reusing existing mobile driver with key: {}", key);
            return mobileDrivers.get(key);
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to initialize mobile driver: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize mobile driver", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static WebDriver initWebDriver(String browser) {
        if (!webDrivers.containsKey(browser)) {
            LOGGER.info("Initializing new web driver for browser: {}", browser);
            WebDriver driver = DriverFactory.createWebDriver(browser);
            webDrivers.put(browser, driver);
            return driver;
        }
        LOGGER.info("Reusing existing web driver for browser: {}", browser);
        return webDrivers.get(browser);
    }

    public static void quitDriver(String key) {
        LOGGER.info("Quitting driver with key: {}", key);
        if (mobileDrivers.containsKey(key)) {
            mobileDrivers.get(key).quit();
            mobileDrivers.remove(key);
        }
        if (webDrivers.containsKey(key)) {
            webDrivers.get(key).quit();
            webDrivers.remove(key);
        }
    }

    public static void quitAll() {
        LOGGER.info("Quitting all drivers");
        for (WebDriver driver : webDrivers.values()) {
            driver.quit();
        }
        webDrivers.clear();
        for (AppiumDriver driver : mobileDrivers.values()) {
            driver.quit();
        }
        mobileDrivers.clear();
    }

    public static AppiumDriver getMobileDriver(String key) {
        return mobileDrivers.get(key);
    }

    public static WebDriver getWebDriver(String browser) {
        return webDrivers.get(browser);
    }
} 