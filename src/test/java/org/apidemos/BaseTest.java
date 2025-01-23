package org.apidemos;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apidemos.appium.AppiumServerManager;
import org.apidemos.base.BasePage;
import org.apidemos.driver.DriverFactory;
import org.apidemos.utils.PlatformUtils;
import org.apidemos.utils.PropertyUtils;
import org.apidemos.utils.TestUtils;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);

    protected static Map<String, String> strings = new HashMap<>();

    protected static AppiumDriverLocalService appiumService;
    protected static AppiumDriver driver;


    @Parameters({"platformName", "appiumHost", "appiumPort"})
    @BeforeTest
    public void beforeTest(@Optional("ANDROID") String platformName,
                           @Optional("127.0.0.1") String host,
                           @Optional("4723") int port) {
        PlatformUtils.setPlatform(platformName);
        setupAppiumService(host, port);
        setupDriver();
        loadStrings(PropertyUtils.getProperty("stringsXml"));
    }

    private void setupAppiumService(String host, int port) {
        appiumService = AppiumServerManager.startAppiumService(host, port);
    }

    private void setupDriver() {
        driver = DriverFactory.getDriver();
    }

    private void loadStrings(String xmlFileName) {
        try (InputStream stringsXml = getClass().getClassLoader().getResourceAsStream(xmlFileName)) {
            if (stringsXml != null) {
                strings = PropertyUtils.parseStringXML(stringsXml);
            } else {
                LOGGER.error("[{}] file not found", xmlFileName);
                throw new IOException(xmlFileName + " file not found");
            }
        } catch (Exception e) {
            LOGGER.error("Problem with file [{}] loading", xmlFileName);
        }
    }

    @BeforeMethod
    public void setUp() {
//        new BasePage().closeApp(PropertyUtils.getProperty("androidAppPackage"), driver);
//        new BasePage().openApp(PropertyUtils.getProperty("androidAppPackage"), driver);
        //TODO Create additional method in TestUtils for startRecordingScreen
        ((CanRecordScreen) driver).startRecordingScreen();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        try {
            if (driver != null) {
                TestUtils.getScreenshotOnFailedMethod(result.getStatus(), result.getName());
                TestUtils.stopVideoRecording(driver, result.getStatus(), result.getName());
                new BasePage().closeApp(PropertyUtils.getProperty("androidAppPackage"), driver);
                DriverFactory.quitDriver();
            }
        } catch (Exception e) {
            LOGGER.error("Error during teardown: {}", e.getMessage());
        }
    }


    @AfterTest
    public void afterTest() {
        try {
            AppiumServerManager.stopAppiumService();
            LOGGER.info("Test execution completed. Appium service stopped.");
        } catch (Exception e) {
            LOGGER.error("Error while stopping Appium service: {}", e.getMessage());
        }
    }

}
