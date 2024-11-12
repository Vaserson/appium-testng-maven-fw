package org.apidemos;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apidemos.utils.PropertyUtils;
import org.apidemos.utils.TestUtils;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

public class BaseTest {
    protected AndroidDriver driver;
    protected WebDriverWait wait;
    InputStream stringsXml;
    protected static HashMap<String, String> strings = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);

    @BeforeTest
    public void beforeTest() throws IOException {
        try {
            String xmlFileName = "strings.xml"; //TODO Move to constants
            stringsXml = getClass().getClassLoader().getResourceAsStream(xmlFileName);
            strings = PropertyUtils.parseStringXML(stringsXml);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stringsXml != null) {
                stringsXml.close();
            }
        }
    }

    @BeforeMethod
    public void setUp() {
        try {

            URL url = new URI(PropertyUtils.getProperty("appiumURL")).toURL();

            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName("Android")
                    .setAppPackage(PropertyUtils.getProperty("androidAppPackage"))
                    .setAppActivity(PropertyUtils.getProperty("androidAppActivity"))
                    .noReset();

            driver = new AndroidDriver(url, options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        try {
            driver.quit();
        } catch (Exception ignored) {}

        TestUtils.getScreenshotOnFailedMethod(result.getStatus(), result.getName());
    }

}
