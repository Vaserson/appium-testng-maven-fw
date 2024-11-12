package org.apidemos;

import io.appium.java_client.AppiumBy;
import org.apidemos.driver.DriverFactory;
import org.apidemos.listeners.TestListener;
import org.apidemos.utils.TestUtils;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;

//@Listeners(TestListener.class)
public class FirstTest extends BaseTest{

    @Test
    public void firstTest() {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(TestUtils.WAIT));
        System.out.println("First test");
        wait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.accessibilityId("App"))).click();
    }

}
