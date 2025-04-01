package org.app.gogopizza;

import org.app.pages.gogopizza.MainPage;
import org.app.pages.gogopizza.MenuPage;
import org.app.pages.gogopizza.ShopPage;
import org.app.utils.PropertyUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

//@Listeners(TestListener.class)
public class GoGoPizzaTest extends GoGoPizzaBaseTest {

    private ShopPage shopPage;
    private MainPage mainPage;
    private MenuPage menuPage;

    @BeforeMethod
    public void beforeMethod() {
        shopPage = new ShopPage(driver);
        mainPage = new MainPage(driver);
        menuPage = new MenuPage(driver);
        
        if(shopPage.isCartVisible()) {
            shopPage.openCart()
                    .emptyCart();
        }
    }

    @Test
    public void checkVersionTest() {
        String aboutText = menuPage
                .openMenu()
                .openMenuAbout()
                .getAboutText();
                
        Assert.assertTrue(aboutText.contains(PropertyUtils.getProperty("version")),
                "Version text should contain expected version");
    }

    @Test
    public void addSushi() {
        mainPage.opemDarnytskyiShop()
                .swipeToProduct()
                .openProduct()
                .addToCart();
                
        Assert.assertTrue(shopPage.isCartVisible(),
                "Cart should be visible after adding product");
    }

}