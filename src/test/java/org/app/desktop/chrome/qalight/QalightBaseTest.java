package org.app.desktop.chrome.qalight;

import org.app.base.BaseTest;
import org.app.driver.DriverManager;
import org.app.enums.Platform;
import org.app.utils.PlatformUtils;
import org.app.utils.PropertyUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

public abstract class QalightBaseTest extends BaseTest {
    
    protected WebDriver driver;

    @BeforeTest
    public void setup() {
        System.setProperty("config.file", "properties/qalight.properties");
        PlatformUtils.setPlatform(Platform.WEB);
        driver = DriverManager.initWebDriver("chrome");
    }

    @BeforeMethod
    public void setupMethod() {
        driver.navigate().to(PropertyUtils.getProperty("web.base.url"));
    }
} 