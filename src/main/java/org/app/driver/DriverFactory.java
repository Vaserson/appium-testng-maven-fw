package org.app.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.AutomationName;
import org.app.appium.AppiumServerManager;
import org.app.utils.PropertyUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;

public class DriverFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {}

    public static AppiumDriver createMobileDriver(String key) throws MalformedURLException, URISyntaxException {
        String platform = PropertyUtils.getProperty("platform");
        URL url = getAppiumURL();
        
        switch (platform.toLowerCase()) {
            case "android":
                return createAndroidDriver(url, key);
            case "ios":
                return createIOSDriver(url, key);
            default:
                throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
    }

    public static WebDriver createWebDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                return createChromeDriver();
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private static URL getAppiumURL() throws MalformedURLException, URISyntaxException {
        String appiumURL = AppiumServerManager.getAppiumServiceUrl();
        if (appiumURL == null || appiumURL.isEmpty()) {
            String defaultAppiumURL = "http://127.0.0.1:4723";
            LOGGER.warn("Appium service URL not found in configuration. Using default Appium URL: [{}]", defaultAppiumURL);
            return new URI(defaultAppiumURL).toURL();
        }
        LOGGER.info("Appium service URL retrieved successfully: [{}]", appiumURL);
        return new URI(appiumURL).toURL();
    }

    private static AndroidDriver createAndroidDriver(URL url, String key) {
        LOGGER.info("Creating Android driver with key: {}", key);
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2)
                .setAppPackage(PropertyUtils.getProperty("androidAppPackage"))
                .setAppActivity(PropertyUtils.getProperty("androidAppActivity"))
                .setNewCommandTimeout(Duration.ofSeconds(60))
                .noReset();

        return new AndroidDriver(url, options);
    }

    private static IOSDriver createIOSDriver(URL url, String key) {
        LOGGER.info("Creating iOS driver with key: {}", key);
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName("iOS")
                .setAutomationName(AutomationName.IOS_XCUI_TEST)
                .setBundleId(PropertyUtils.getProperty("iosBundleId"))
                .setNewCommandTimeout(Duration.ofSeconds(60))
                .noReset();

        return new IOSDriver(url, options);
    }

    private static WebDriver createChromeDriver() {
        LOGGER.info("Creating Chrome driver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        // Selenium Manager since 4.6 automatically downloads drivers
        return new ChromeDriver(options);
    }
}