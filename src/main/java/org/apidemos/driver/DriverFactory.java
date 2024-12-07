package org.apidemos.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apidemos.utils.PropertyUtils;

import java.net.URI;
import java.net.URL;

public class DriverFactory {
    private static AppiumDriver driver;

    public static AppiumDriver getDriver() {
        try {
            URL url = new URI(PropertyUtils.getProperty("appiumURL")).toURL();

            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName("Android")
                    .setAppPackage(PropertyUtils.getProperty("androidAppPackage"))
                    .setAppActivity(PropertyUtils.getProperty("androidAppActivity"))
                    .noReset();

            driver = new AndroidDriver(url, options);

            driver.setSetting("imageMatchThreshold", "0.6");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driver;
    }
}
