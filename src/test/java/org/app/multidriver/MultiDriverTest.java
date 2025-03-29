package org.app.multidriver;

import io.appium.java_client.AppiumDriver;
import org.app.base.BasePage;
import org.app.driver.DriverFactory;
import org.app.utils.PropertyUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class MultiDriverTest extends BaseTest{
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiDriverTest.class);

    @Test
    public void testMultiDriverScenario() {
        // Use Android Native App Driver
        AppiumDriver androidNativeAppDriver = DriverFactory.getAndroidNativeAppDriver();
        new BasePage(androidNativeAppDriver).openApp(PropertyUtils.getProperty("androidAppPackage"));
        System.out.println(new BasePage(androidNativeAppDriver).findElementsByImage("API_DEMOS.A_IMAGE"));
        LOGGER.info("Interacted with Android Native App.");

        // Switch to Android Chrome Driver
        AppiumDriver androidChromeDriver = DriverFactory.getAndroidChromeDriver();
        androidChromeDriver.get("https://www.google.com");
        LOGGER.info("Opened URL in Android Chrome.");

        // Switch to Desktop Web Driver
        WebDriver desktopWebDriver = DriverFactory.getDesktopChromeDriver();
        desktopWebDriver.get("https://www.google.com");
        LOGGER.info("Opened URL in Desktop Web Browser.");
        Assert.assertTrue(false);
    }

    @AfterClass
    public void tearDown() {
        // Quit all drivers
        DriverFactory.quitAllDrivers();
    }
}