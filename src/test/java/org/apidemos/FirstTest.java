package org.apidemos;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

public class FirstTest extends BaseTest{

    @Test
    public void firstTest() {
        System.out.println("First test");
        wait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.accessibilityId("App"))).click();
    }

}
