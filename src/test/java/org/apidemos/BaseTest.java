package org.apidemos;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.net.URI;
import java.net.URL;
import java.time.Duration;

public class BaseTest {
    protected AndroidDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        try {
            URL url = new URI("http://127.0.0.1:4723").toURL();

            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName("Android")
                    .setAppPackage("io.appium.android.apis")
                    .setAppActivity("io.appium.android.apis.ApiDemos")
                    .noReset();

            driver = new AndroidDriver(url, options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() {
        try {
            driver.quit();
        } catch (Exception ignored) {}
    }

}
