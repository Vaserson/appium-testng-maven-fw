package org.apidemos;

import org.apidemos.pages.MenuPage;
import org.apidemos.pages.ViewsButtonsPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

//@Listeners(TestListener.class)
public class FirstTest extends BaseTest{

    @Test
    public void firstTest() {
        new MenuPage()
                .openMenu("Views")
                .openMenu("Buttons");

        SoftAssert softAssert = new SoftAssert();

        ViewsButtonsPage viewsButtonsPage = new ViewsButtonsPage();
        softAssert.assertTrue(viewsButtonsPage.checkToggleButtonState("OFF"));
        viewsButtonsPage.tapToggleButton();
        softAssert.assertTrue(viewsButtonsPage.checkToggleButtonState("ON"));

        softAssert.assertAll();
    }

}
