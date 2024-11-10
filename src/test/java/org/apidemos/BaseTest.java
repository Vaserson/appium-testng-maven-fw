package org.apidemos;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

public class BaseTest {
    protected AndroidDriver driver;
    protected WebDriverWait wait;
    protected Properties properties;
    InputStream inputStream;

    @BeforeMethod
    public void setUp() {
        try {
            properties = new Properties();
            String propertiesFileName = "apiDemos.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
            properties.load(inputStream);

            URL url = new URI(properties.getProperty("appiumURL")).toURL();

            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName("Android")
                    .setAppPackage(properties.getProperty("androidAppPackage"))
                    .setAppActivity(properties.getProperty("androidAppActivity"))
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
