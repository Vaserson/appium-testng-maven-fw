package org.app.pages.web.qalight;

import org.app.base.BasePage;
import org.app.base.MobilePage;
import org.openqa.selenium.WebDriver;

public class MainPage extends BasePage {

    public MainPage(WebDriver driver) {
        super(driver);
    }
    
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    public NewsPage openNewsPage() {
        click("NEWS_LINK");
        return new NewsPage(driver);
    }
}