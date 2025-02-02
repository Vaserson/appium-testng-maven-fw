package org.app.apidemos;

import org.app.base.BasePage;
import org.app.pages.apidemos.MenuPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

//@Listeners(TestListener.class)
public class FirstTest extends BaseTest{


    @Test
    public void firstLevelMenuTest() {
        new MenuPage()
                .openAccess_ibilityMenu().clickBackButton()
                .openAccessibilityMenu().clickBackButton()
                .openAnimationMenu().clickBackButton()
                .openAppMenu().clickBackButton()
                .openContentMenu().clickBackButton()
                .openGraphicsMenu().clickBackButton()
                .openMediaMenu().clickBackButton()
                .openNfcMenu().clickBackButton()
                .openOsMenu().clickBackButton()
                .openPreferenceMenu().clickBackButton()
                .openTextMenu().clickBackButton()
                .openViewsMenu().clickBackButton();
    }

    @Test
    public void firstTest() {
        new MenuPage()
                .openMenu("Views")
                .openMenu("Buttons");

        SoftAssert softAssert = new SoftAssert();

        BasePage basePage = new BasePage();
        basePage.click("API_DEMOS.VIEWS.BUTTONS.NORMAL_BUTTON");
        basePage.click("API_DEMOS.VIEWS.BUTTONS.SMALL_BUTTON");
        softAssert.assertTrue(basePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.OFF"));
        basePage.click("API_DEMOS.VIEWS.BUTTONS.TOGGLE_BUTTON");
        softAssert.assertFalse(basePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.OFF"));
        softAssert.assertTrue(basePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.ON"));
        basePage.click("API_DEMOS.VIEWS.BUTTONS.TOGGLE_BUTTON");
        softAssert.assertFalse(basePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.ON"));
        softAssert.assertTrue(basePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.OFF"));

/*        ViewsButtonsPage viewsButtonsPage = new ViewsButtonsPage();
        softAssert.assertTrue(viewsButtonsPage.checkToggleButtonState("OFF"));
        viewsButtonsPage.tapToggleButton();
        softAssert.assertTrue(viewsButtonsPage.checkToggleButtonState("ON"));*/

        softAssert.assertAll();
    }

}
