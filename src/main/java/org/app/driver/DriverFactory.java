package org.app.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.Setting;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.appium.AppiumServerManager;
import org.app.exceptions.FrameworkException;
import org.app.exceptions.UnsupportedPlatformException;
import org.app.utils.PropertyUtils;

import java.net.URI;
import java.net.URL;

public class DriverFactory {
    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);

    private static AppiumDriver driver;

    private static final String PLATFORM = PropertyUtils.getProperty("platform").toLowerCase();
    private static final String APP_PACKAGE = PropertyUtils.getProperty("androidAppPackage");
    private static final String APP_ACTIVITY = PropertyUtils.getProperty("androidAppActivity");
    private static final String IOS_BUNDLE_ID = PropertyUtils.getProperty("iosBundleId");

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

                switch (PLATFORM) {
                    case "android":
                        driver = createAndroidDriver(url);
                        break;
                    case "ios":
                        driver = createIOSDriver(url);
                        break;
                    default:
                        throw new UnsupportedPlatformException("Platform not supported: " + PLATFORM);
                }

                LOGGER.info("Appium Driver initialized successfully for platform: [{}]", PLATFORM);
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Appium Driver. Error: {}", e.getMessage());
                throw new FrameworkException("Failed to initialize Appium Driver", e);
            }
        }
        driver.setSetting(Setting.IMAGE_MATCH_THRESHOLD, "0.85");
        return driver;
    }

    private static AppiumDriver createAndroidDriver(URL url) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAppPackage(APP_PACKAGE)
                .setAppActivity(APP_ACTIVITY)
                .noReset();

        LOGGER.info("Creating Android driver with App Package: [{}] and App Activity: [{}]", APP_PACKAGE, APP_ACTIVITY);
        return new AndroidDriver(url, options);
    }

    private static AppiumDriver createIOSDriver(URL url) {
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName("iOS")
                .setBundleId(IOS_BUNDLE_ID)
                .noReset();

        LOGGER.info("Creating iOS driver with Bundle ID: [{}]", IOS_BUNDLE_ID);
        return new IOSDriver(url, options);
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