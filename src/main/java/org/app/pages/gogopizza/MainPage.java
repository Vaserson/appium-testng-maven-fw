package org.app.pages.gogopizza;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.base.BasePage;
import org.app.utils.TestUtils;

import java.util.Objects;


public class MainPage extends BasePage {
    private static final Logger LOGGER = LogManager.getLogger(MainPage.class);


    public MainPage returnToMainPageIfNot() {
        LOGGER.info("Returning to the Main page.");
        int retry = 0;
        while (waitForElementsToBeVisible("GOGO_PIZZA.MAIN.PROMOTION_IMAGE").isEmpty() || TestUtils.RETRY > retry) {
            click("df");
            retry++;
        }
        return this;
    }

    public ShopPage openPodilskyiShop() {
        click("GOGO_PIZZA.MAIN.PODILSKYI");
        return new ShopPage();
    }

    public ShopPage opemDarnytskyiShop() {
        click("GOGO_PIZZA.MAIN.DARNYTSKYI");
        return new ShopPage();
    }

}