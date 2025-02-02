package org.app.pages.gogopizza;

import org.app.base.BasePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ItemPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPage.class);


    public ShopPage addToCart() {
        LOGGER.info("Clicking Add to cart button");
        click("GOGO_PIZZA.SHOP.ITEM.ADD_BUTTON");
        return new ShopPage();
    }

}