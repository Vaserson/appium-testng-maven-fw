package org.apidemos;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apidemos.appium.AppiumServerManager;
import org.apidemos.driver.DriverFactory;
import org.apidemos.utils.PlatformUtils;
import org.apidemos.utils.PropertyUtils;
import org.apidemos.utils.TestUtils;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class BaseTest {
    InputStream stringsXml;
    protected static HashMap<String, String> strings = new HashMap<>();

    protected static AppiumDriverLocalService appiumService;
    protected static AppiumDriver driver;

    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);

    @Parameters({"platformName"})
    @BeforeTest
    public void beforeTest(@Optional("ANDROID") String platformName) throws IOException {
        appiumService = AppiumServerManager.startAppiumService("127.0.0.1", 4723);
        driver = DriverFactory.getDriver();
        PlatformUtils.setPlatform(platformName);
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
        ((CanRecordScreen) driver).startRecordingScreen();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        try {
            DriverFactory.getDriver().quit();
        } catch (Exception ignored) {}

        TestUtils.getScreenshotOnFailedMethod(result.getStatus(), result.getName());
        TestUtils.stopVideoRecording(driver, result.getStatus(), result.getName());
    }

}
