package org.app.methods;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

public class MethodTest extends BaseTest{
    private static final Logger LOGGER = LogManager.getLogger(MethodTest.class);

    @Test
    public void methodTest() {
        BasePage basePage = new BasePage();
        By locator = AppiumBy.xpath("//*[@text='NFC']");
        By viewsMenu = AppiumBy.xpath("//*[@text='Views']");
        By textFieldsMenu = AppiumBy.xpath("//*[@text='TextFields']");
        By textField1 = AppiumBy.xpath("(//android.widget.EditText)[1]");
        By textField2 = AppiumBy.xpath("(//android.widget.EditText)[2]");
        By textField3 = AppiumBy.xpath("(//android.widget.EditText)[3]");
        By textField4 = AppiumBy.xpath("(//android.widget.EditText)[4]");
        By textField5 = AppiumBy.xpath("(//android.widget.EditText)[5]");


        System.out.println(basePage.findElementsByImage("API_DEMOS.A_IMAGE"));

 /*
*//*        System.out.println(strings.get("home_title"));
        basePage.click(viewsMenu);
        basePage.findElementByDynamicText("NONE");
*//*

*//*

        System.out.println(ScreenUtils.getScreenCenter(driver));
        basePage.dragFromPointToPoint(500,2000,500,500);

        basePage.manageNotifications(true);
        basePage.waitInSeconds(2);
        basePage.manageNotifications(false);
*//*


        basePage.click("API_DEMOS.VIEWS");
        basePage.scrollToElementByText("TextFields", "Down");

        LOGGER.info("--------------------------------");
        LOGGER.info("getCoordinatesAndClick(textFieldsMenu)");
        basePage.getCoordinatesAndClick(textFieldsMenu);


        LOGGER.info("--------------------------------");
        LOGGER.info("sendKeys(textField1, 'One')");
        basePage.sendKeys(textField1, "One");

        basePage.waitInSeconds(2);

        LOGGER.info("--------------------------------");
        LOGGER.info("clear(textField1)");
        basePage.clear(textField1);

        LOGGER.info("--------------------------------");
        LOGGER.info("sendKeys(textField2, 'Date and time')");
        basePage.sendKeys(textField2, basePage.getDateTime());

        LOGGER.info("--------------------------------");
        LOGGER.info("sendKeys(textField3, 'Date and time')");
        basePage.sendKeys(textField3, basePage.getDateTime());

        LOGGER.info("--------------------------------");
        LOGGER.info("sendKeys(textField4, 'Date and time')");
        basePage.sendKeys(textField4, basePage.getDateTime());

        LOGGER.info("--------------------------------");
        LOGGER.info("sendKeys(textField5, 'Date and time')");
        basePage.sendKeys(textField5, basePage.getDateTime());

        basePage.waitInSeconds(2);
        basePage.pressAndroidButton(AndroidKey.BACK);
        basePage.pressAndroidButton(AndroidKey.BACK);

        LOGGER.info("--------------------------------");
        LOGGER.info("click(locator)");
        basePage.click("API_DEMOS.NFC");
        basePage.pressAndroidButton(AndroidKey.BACK);
        LOGGER.info("--------------------------------");
        LOGGER.info("click(locator, 5)");
        basePage.click("API_DEMOS.NFC", 5);
        basePage.pressAndroidButton(AndroidKey.BACK);
        LOGGER.info("--------------------------------");
        LOGGER.info("findElementByDynamicText(\"NFC\")");
        WebElement element = basePage.findElementByDynamicText("NFC");
        LOGGER.info("--------------------------------");
        LOGGER.info("click(element)");
        basePage.click(element);
        basePage.pressAndroidButton(AndroidKey.BACK);
        LOGGER.info("--------------------------------");
        LOGGER.info("click(NONE, NULL, locator)");
        basePage.click(
                AppiumBy.xpath("//*[@text='NONE']"),
                AppiumBy.xpath("//*[@text='NULL']"),
                locator);
        basePage.pressAndroidButton(AndroidKey.BACK);
        LOGGER.info("--------------------------------");
        LOGGER.info("clickWhileExist(locator)");
        basePage.clickWhileExist(locator);
        basePage.pressAndroidButton(AndroidKey.BACK);
        LOGGER.info("--------------------------------");
        LOGGER.info("clickIfExist(NONE)");
        basePage.clickIfExist(AppiumBy.xpath("//*[@text='NONE']"));
        LOGGER.info("--------------------------------");
        LOGGER.info("clickIfExist(locator)");
        basePage.clickIfExist(locator);
        basePage.pressAndroidButton(AndroidKey.BACK);
        LOGGER.info("--------------------------------");
        LOGGER.info("clickWhileExist(locator)");
        basePage.clickWhileExist(locator);
        basePage.pressAndroidButton(AndroidKey.BACK);
        LOGGER.info("--------------------------------");
        LOGGER.info("clickWhileExist(//*[@text='API Demos']");
        basePage.clickWhileExist(AppiumBy.xpath("//*[@text='API Demos']"), 10);

*//*
        basePage.waitForInvisibility(locator, 5);

        basePage.findElementByDynamicXpath("//*[@text='NFC']");
        basePage.findElementByDynamicText("NFC");
        basePage.findByAccessibilityId("NFC");

        WebElement element = basePage.findElementByDynamicText("NFC");
        basePage.click(element);
        basePage.pressAndroidButton(AndroidKey.BACK);
        basePage.getCoordinates(locator);
*/
    }

}
