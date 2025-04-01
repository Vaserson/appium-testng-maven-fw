package org.app.pages.multidriver;

import org.app.base.BasePage;
import org.app.utils.LocatorUtils;
import org.openqa.selenium.WebDriver;

public class WebAppPage extends BasePage {
    
    public WebAppPage(WebDriver driver) {
        super(driver);
    }
    
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    public void performWebAction() {
        sendKeys(getLocator(LocatorUtils.getLocator("SEARCH_BOX")), "Appium Test");
        click("SEARCH_BUTTON");
    }
    
    public boolean isPageResponsive() {
        return isElementVisible("SEARCH_BOX");
    }
} 