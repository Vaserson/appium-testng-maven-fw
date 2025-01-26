package org.app.pages.gogopizza;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.base.BasePage;


public class ItemPage extends BasePage {
    private static final Logger LOGGER = LogManager.getLogger(ItemPage.class);


    public ShopPage addToCart() {
        LOGGER.info("Clicking Add to cart button");
        click("GOGO_PIZZA.SHOP.ITEM.ADD_BUTTON");
        return new ShopPage();
    }

}