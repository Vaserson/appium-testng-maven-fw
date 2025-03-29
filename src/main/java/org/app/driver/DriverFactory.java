package org.app.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.Setting;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.appium.java_client.remote.AutomationName;
import org.app.appium.AppiumServerManager;
import org.app.exceptions.FrameworkException;
import org.app.exceptions.UnsupportedPlatformException;
import org.app.utils.PropertyUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;

public class DriverFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverFactory.class);

    private static AppiumDriver androidNativeAppDriver;
    private static AppiumDriver androidChromeDriver;
    private static AppiumDriver iosNativeAppDriver;
    private static AppiumDriver iosSafariDriver;
    private static WebDriver desktopChromeDriver;

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

    private static AppiumDriver createAndroidNativeAppDriver(URL url) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2)
                .noReset();
        LOGGER.info("Creating Android Native App Driver");
        return new AndroidDriver(url, options);
    }

    public static AppiumDriver getAndroidNativeAppDriver() {
        if (androidNativeAppDriver == null) {
            try {
                String appiumURL = getAppiumURL();
                URL url = new URI(appiumURL).toURL();
                androidNativeAppDriver = createAndroidNativeAppDriver(url);
                LOGGER.info("Android Native App Driver initialized successfully.");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Android Native App Driver. Error: {}", e.getMessage());
                throw new RuntimeException("Failed to initialize Android Native App Driver", e);
            }
        }
        return androidNativeAppDriver;
    }

    private static AppiumDriver createAndroidChromeDriver(URL url) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .withBrowserName("Chrome")
                .setAutomationName(AutomationName.ANDROID_UIAUTOMATOR2)
                .noReset();
        LOGGER.info("Creating Android Chrome Driver.");
        return new AndroidDriver(url, options);
    }

    public static AppiumDriver getAndroidChromeDriver() {
        if (androidChromeDriver == null) {
            try {
                String appiumURL = getAppiumURL();
                URL url = new URI(appiumURL).toURL();
                androidChromeDriver = createAndroidChromeDriver(url);
                LOGGER.info("Android Chrome Driver initialized successfully.");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Android Chrome Driver. Error: {}", e.getMessage());
                throw new RuntimeException("Failed to initialize Android Chrome Driver", e);
            }
        }
        return androidChromeDriver;
    }

    private static AppiumDriver createIOSNativeAppDriver(URL url) {
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName("iOS")
                .setAutomationName(AutomationName.IOS_XCUI_TEST)
                .noReset();
        LOGGER.info("Creating iOS Native App Driver");
        return new IOSDriver(url, options);
    }

    public static AppiumDriver getIOSNativeAppDriver() {
        if (iosNativeAppDriver == null) {
            try {
                String appiumURL = getAppiumURL();
                URL url = new URI(appiumURL).toURL();
                iosNativeAppDriver = createIOSNativeAppDriver(url);
                LOGGER.info("iOS Native App Driver initialized successfully.");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize iOS Native App Driver. Error: {}", e.getMessage());
                throw new RuntimeException("Failed to initialize iOS Native App Driver", e);
            }
        }
        return iosNativeAppDriver;
    }

    private static AppiumDriver createIOSSafariDriver(URL url) {
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName("iOS")
                .withBrowserName("Safari")
                .setAutomationName(AutomationName.IOS_XCUI_TEST)
                .noReset();
        LOGGER.info("Creating iOS Safari Driver.");
        return new IOSDriver(url, options);
    }

    public static AppiumDriver getIOSSafariDriver() {
        if (iosSafariDriver == null) {
            try {
                String appiumURL = getAppiumURL();
                URL url = new URI(appiumURL).toURL();
                iosSafariDriver = createIOSSafariDriver(url);
                LOGGER.info("iOS Safari Driver initialized successfully.");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize iOS Safari Driver. Error: {}", e.getMessage());
                throw new RuntimeException("Failed to initialize iOS Safari Driver", e);
            }
        }
        return iosSafariDriver;
    }

    private static WebDriver createDesktopChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver-win64/chromedriver.exe");
        LOGGER.info("Creating Desktop Web Driver (Chrome).");
        return new ChromeDriver(options);
    }

    public static WebDriver getDesktopChromeDriver() {
        if (desktopChromeDriver == null) {
            desktopChromeDriver = createDesktopChromeDriver();
            LOGGER.info("Desktop Chrome Driver initialized successfully.");
        }
        return desktopChromeDriver;
    }

    public static void quitAllDrivers() {
        if (androidNativeAppDriver != null) {
            androidNativeAppDriver.quit();
            LOGGER.info("Android Native App Driver quit successfully.");
            androidNativeAppDriver = null;
        }
        if (androidChromeDriver != null) {
            androidChromeDriver.quit();
            LOGGER.info("Android Chrome Driver quit successfully.");
            androidChromeDriver = null;
        }
        if (iosNativeAppDriver != null) {
            iosNativeAppDriver.quit();
            LOGGER.info("iOS Native App Driver quit successfully.");
            iosNativeAppDriver = null;
        }
        if (iosSafariDriver != null) {
            iosSafariDriver.quit();
            LOGGER.info("iOS Safari Driver quit successfully.");
            iosSafariDriver = null;
        }
        if (desktopChromeDriver != null) {
            desktopChromeDriver.quit();
            LOGGER.info("Desktop Chrome Driver quit successfully.");
            desktopChromeDriver = null;
        }
    }
}