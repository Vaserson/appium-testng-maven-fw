package org.app.pages.gogopizza;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.base.BasePage;
import org.app.utils.TestUtils;

import java.util.Objects;


public class ShopPage extends BasePage {
    private static final Logger LOGGER = LogManager.getLogger(ShopPage.class);


    public ShopPage swipeToProduct() {
        LOGGER.info("Swiping to the product on the Shop page.");
        scrollToElement("GOGO_PIZZA.SHOP.PHILADELPHIA_SALMON_ROYAL", "Down", 40);
        return this;
    }

    public ItemPage openProduct() {
        click("GOGO_PIZZA.SHOP.PHILADELPHIA_SALMON_ROYAL");
        return new ItemPage();
    }

    public boolean isCartVisible() {
        return isElementVisible("GOGO_PIZZA.SHOP.CART_BUTTON");
    }

    public CartPage openCart() {
        click("GOGO_PIZZA.SHOP.CART_BUTTON");
        return new CartPage();
    }

}