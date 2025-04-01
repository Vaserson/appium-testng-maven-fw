package org.app.apidemos;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.app.appium.AppiumServerManager;
import org.app.base.BaseTest;
import org.app.base.MobilePage;
import org.app.driver.DriverManager;
import org.app.enums.Platform;
import org.app.utils.PlatformUtils;
import org.app.utils.PropertyUtils;
import org.app.utils.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

public class ApiDemosBaseTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDemosBaseTest.class);

    protected AppiumDriver driver;
    private String appPackage;

    @BeforeTest
    public void setup() {
        System.setProperty("config.file", "properties/apidemos.properties");
        PlatformUtils.setPlatform(Platform.ANDROID);
        AppiumServerManager.startAppiumService("127.0.0.1", 4723);
        driver = DriverManager.initMobileDriver("androidNative");
        appPackage = PropertyUtils.getProperty("androidAppPackage");
    }

    @BeforeMethod
    public void setupMethod() {
        driver = DriverManager.initMobileDriver("androidNative");
        MobilePage mobilePage = new MobilePage(driver);
        mobilePage.closeApp(appPackage);
        mobilePage.openApp(appPackage);
        ((CanRecordScreen) driver).startRecordingScreen();
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        try {
            if (driver != null) {
                TestUtils.getScreenshotOnFailedMethod(driver, result.getStatus(), result.getName());
                TestUtils.stopVideoRecording(driver, result.getStatus(), result.getName());
            }
        } catch (Exception e) {
            LOGGER.error("Error during teardown: {}", e.getMessage());
        }
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        AppiumServerManager.stopAppiumService();
    }
} 