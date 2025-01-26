package org.app.pages.gogopizza;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.base.BasePage;


public class CartPage extends BasePage {
    private static final Logger LOGGER = LogManager.getLogger(CartPage.class);


    public ShopPage addToCart() {
        LOGGER.info("Clicking Add to cart button");
        click("GOGO_PIZZA.SHOP.ITEM.ADD_BUTTON");
        return new ShopPage();
    }

    public ShopPage emptyCart() {
        LOGGER.info("Clicking Empty cart button");
        click("GOGO_PIZZA.SHOP.CART.EMPTY_CART_BUTTON");
        click("GOGO_PIZZA.SHOP.CART.EMPTY_CART_BUTTON.YES");
        return new ShopPage();
    }

}