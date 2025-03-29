package org.app.pages.gogopizza;

import io.appium.java_client.AppiumDriver;
import org.app.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShopPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopPage.class);

    public ShopPage(AppiumDriver driver) {
        super(driver);
    }


    public ShopPage swipeToProduct() {
        LOGGER.info("Swiping to the product on the Shop page.");
        scrollToElement("GOGO_PIZZA.SHOP.PHILADELPHIA_SALMON_ROYAL", "Down", 40);
        return this;
    }

    public ItemPage openProduct() {
        click("GOGO_PIZZA.SHOP.PHILADELPHIA_SALMON_ROYAL");
        return new ItemPage((AppiumDriver) driver);
    }

    public boolean isCartVisible() {
        return isElementVisible("GOGO_PIZZA.SHOP.CART_BUTTON");
    }

    public CartPage openCart() {
        click("GOGO_PIZZA.SHOP.CART_BUTTON");
        return new CartPage((AppiumDriver) driver);
    }

}