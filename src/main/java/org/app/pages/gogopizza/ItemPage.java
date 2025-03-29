package org.app.pages.gogopizza;

import io.appium.java_client.AppiumDriver;
import org.app.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ItemPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPage.class);

    public ItemPage(AppiumDriver driver) {
        super(driver);
    }


    public ShopPage addToCart() {
        LOGGER.info("Clicking Add to cart button");
        click("GOGO_PIZZA.SHOP.ITEM.ADD_BUTTON");
        return new ShopPage((AppiumDriver) driver);
    }

}