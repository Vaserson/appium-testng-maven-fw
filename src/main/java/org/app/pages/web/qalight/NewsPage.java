package org.app.pages.web.qalight;

import org.app.base.BasePage;
import org.app.base.MobilePage;
import org.openqa.selenium.WebDriver;

public class NewsPage extends BasePage {

    public NewsPage(WebDriver driver) {
        super(driver);
    }
    
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    public void clickFirstNewsBlock() {
        click("NEWS_BLOCK");
    }
}