package org.app.pages.gogopizza;

import io.appium.java_client.AppiumDriver;
import org.app.base.MobilePage;
import org.app.utils.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainPage extends MobilePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainPage.class);

    public MainPage(AppiumDriver driver) {
        super(driver);
    }


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
        return new ShopPage((AppiumDriver) driver);
    }

    public ShopPage opemDarnytskyiShop() {
        click("GOGO_PIZZA.MAIN.DARNYTSKYI");
        return new ShopPage((AppiumDriver) driver);
    }

}