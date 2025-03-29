package org.app.multidriver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import org.app.appium.AppiumServerManager;
import org.app.base.BasePage;
import org.app.driver.DriverFactory;
import org.app.utils.PlatformUtils;
import org.app.utils.PropertyUtils;
import org.app.utils.TestUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    protected static Map<String, String> strings = new HashMap<>();

    protected static AppiumDriverLocalService appiumService;
    protected static AppiumDriver mobileNativeDriver;
    protected static AppiumDriver mobileChromeDriver;
    protected static WebDriver desktopChromeDriver;


    @Parameters({"platformName", "appiumHost", "appiumPort"})
    @BeforeTest
    public void beforeTest(@Optional("ANDROID") String platformName,
                           @Optional("127.0.0.1") String host,
                           @Optional("4723") int port) {
        System.setProperty("config.file", "src/test/resources/properties/apiDemos.properties");
        PlatformUtils.setPlatform(platformName);
        setupAppiumService(host, port);
        loadStrings(PropertyUtils.getProperty("stringsXml"));
    }

    private void setupAppiumService(String host, int port) {
        appiumService = AppiumServerManager.startAppiumService(host, port);
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
        //TODO Create additional method in TestUtils for startRecordingScreen
//        ((CanRecordScreen) mobileNativeDriver).startRecordingScreen();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        try {
            if (mobileNativeDriver != null) {
//                TestUtils.getScreenshotOnFailedMethod(result.getStatus(), result.getName());
//                TestUtils.stopVideoRecording(mobileNativeDriver, result.getStatus(), result.getName());
//                new BasePage().closeApp(PropertyUtils.getProperty("androidAppPackage"), mobileNativeDriver);
                DriverFactory.quitAllDrivers();
            }
        } catch (Exception e) {
            LOGGER.error("Error during tear down: {}", e.getMessage());
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
