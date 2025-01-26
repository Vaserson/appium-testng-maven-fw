package org.app.pages.gogopizza;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.base.BasePage;


public class MenuPage extends BasePage {

    private static final Logger LOGGER = LogManager.getLogger(MenuPage.class);


    public MenuPage openMenu() {
        LOGGER.info("Clicking the menu button.");
        click(getLocator("GOGO_PIZZA.MENU_BTN"));
        return this;
    }

    public MenuPage openMenuAbout() {
        LOGGER.info("Clicking the About menu.");
        click(getLocator("GOGO_PIZZA.MENU.ABOUT_MENU"));
        return this;
    }

    public String getAboutText() {
        String text = getText(getLocator("GOGO_PIZZA.MENU.ABOUT_MENU.ABOUT_TEXT"));
        LOGGER.info("About text is [{}]", text);
        return text;
    }
}