package org.app.multidriver;

import io.appium.java_client.AppiumDriver;
import org.app.appium.AppiumServerManager;
import org.app.base.MobilePage;
import org.app.driver.DriverManager;
import org.app.enums.Platform;
import org.app.pages.multidriver.WebAppPage;
import org.app.utils.PlatformUtils;
import org.app.utils.PropertyUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class MultiDriverTest {

    protected WebDriver webDriver;
    protected AppiumDriver appiumDriver;
    private String appPackage;

    @AfterTest
    public void tearDown() {
        DriverManager.quitAll();
        AppiumServerManager.stopAppiumService();
    }

    @Test
    public void testDesktopWebScenario() {
        PropertyUtils.loadProperties("properties/google.properties");
        PlatformUtils.setPlatform(Platform.WEB);
        webDriver = DriverManager.initWebDriver("chrome");
        webDriver.navigate().to(PropertyUtils.getProperty("web.base.url"));

        SoftAssert softAssert = new SoftAssert();

        // Test web app functionality
        WebAppPage webAppPage = new WebAppPage(webDriver);
        String webAppTitle = webAppPage.getPageTitle();
        softAssert.assertNotNull(webAppTitle, "Web app title should not be null");

        // Perform web actions
        webAppPage.performWebAction();

        // Verify page is responsive
        softAssert.assertTrue(webAppPage.isPageResponsive(),
                "Web app should remain responsive");



        PropertyUtils.loadProperties("properties/apidemos.properties");
        PlatformUtils.setPlatform(Platform.ANDROID);
        AppiumServerManager.startAppiumService("127.0.0.1", 4723);
        appiumDriver = DriverManager.initMobileDriver("androidNative");
        appPackage = PropertyUtils.getProperty("androidAppPackage");

        MobilePage mobilePage = new MobilePage(appiumDriver);
        mobilePage.closeApp(appPackage);
        mobilePage.openApp(appPackage);

        softAssert.assertAll();

    }
} 