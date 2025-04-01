package org.app.pages.gogopizza;

import io.appium.java_client.AppiumDriver;
import org.app.base.MobilePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MenuPage extends MobilePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuPage.class);

    public MenuPage(AppiumDriver driver) {
        super(driver);
    }


    public MenuPage openMenu() {
        LOGGER.info("Clicking the menu button.");
        click("GOGO_PIZZA.MENU_BTN");
        return this;
    }

    public MenuPage openMenuAbout() {
        LOGGER.info("Clicking the About menu.");
        click("GOGO_PIZZA.MENU.ABOUT_MENU");
        return this;
    }

    public String getAboutText() {
        String text = getText("GOGO_PIZZA.MENU.ABOUT_MENU.ABOUT_TEXT");
        LOGGER.info("About text is [{}]", text);
        return text;
    }
}