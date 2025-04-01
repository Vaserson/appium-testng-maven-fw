package org.app.pages.multidriver;

import io.appium.java_client.AppiumDriver;
import org.app.base.MobilePage;

public class NativeAppPage extends MobilePage {
    
    public NativeAppPage(AppiumDriver driver) {
        super(driver);
    }
    
    public String getAppTitle() {
        return findElementByImage("API_DEMOS.A_IMAGE").getText();
    }
    
    public void performNativeAction() {
        findElementByImage("API_DEMOS.A_IMAGE").click();
    }
    
    public boolean isAppResponsive() {
        try {
            return findElementByImage("API_DEMOS.A_IMAGE").isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
} 