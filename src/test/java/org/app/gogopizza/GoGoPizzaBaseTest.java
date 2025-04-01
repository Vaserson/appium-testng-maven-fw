package org.app.gogopizza;

import io.appium.java_client.AppiumDriver;
import org.app.appium.AppiumServerManager;
import org.app.base.BaseTest;
import org.app.base.MobilePage;
import org.app.driver.DriverManager;
import org.app.enums.Platform;
import org.app.utils.PlatformUtils;
import org.app.utils.PropertyUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

public class GoGoPizzaBaseTest extends BaseTest {
    
    protected AppiumDriver driver;
    private String appPackage;

    @BeforeTest
    public void setup() {
        System.setProperty("config.file", "properties/gogopizza.properties");
        PlatformUtils.setPlatform(Platform.WEB);
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
    }
} 