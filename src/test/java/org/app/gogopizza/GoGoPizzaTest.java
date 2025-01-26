package org.app.gogopizza;

import org.app.pages.gogopizza.MainPage;
import org.app.pages.gogopizza.MenuPage;
import org.app.pages.gogopizza.ShopPage;
import org.app.utils.PropertyUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

//@Listeners(TestListener.class)
public class GoGoPizzaTest extends BaseTest {

    @BeforeMethod
    public void beforeMethod() {
        ShopPage shopPage = new ShopPage();
        if(shopPage.isCartVisible()) {
            shopPage.openCart()
                    .emptyCart();
        }
        Assert.assertFalse(shopPage.isCartVisible());
    }

    @Test
    public void checkVersionTest() {
        String aboutText = new MenuPage()
                .openMenu()
                .openMenuAbout()
                .getAboutText();
        Assert.assertTrue(aboutText.contains(PropertyUtils.getProperty("version")));
    }

    @Test
    public void addSushi() {
        Assert.assertTrue(new MainPage()
                .opemDarnytskyiShop()
                .swipeToProduct()
                .openProduct()
                .addToCart()
                .isCartVisible());
    }

}