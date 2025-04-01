package org.app.apidemos;

import org.app.base.MobilePage;
import org.app.pages.apidemos.MenuPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

//@Listeners(TestListener.class)
public class FirstTest extends ApiDemosBaseTest {

    @Test
    public void firstLevelMenuTest() {
        new MenuPage(driver)
                .openAccess_ibilityMenu().clickBackButton()
/*                .openAccessibilityMenu().clickBackButton()
                .openAnimationMenu().clickBackButton()
                .openAppMenu().clickBackButton()
                .openContentMenu().clickBackButton()
                .openGraphicsMenu().clickBackButton()
                .openMediaMenu().clickBackButton()
                .openNfcMenu().clickBackButton()
                .openOsMenu().clickBackButton()
                .openPreferenceMenu().clickBackButton()
                .openTextMenu().clickBackButton()*/
                .openViewsMenu().clickBackButton();
    }

    @Test
    public void firstTest() {
        new MenuPage(driver)
                .openMenu("Views")
                .openMenu("Buttons");

        SoftAssert softAssert = new SoftAssert();

        MobilePage mobilePage = new MobilePage(driver);
        mobilePage.click("API_DEMOS.VIEWS.BUTTONS.NORMAL_BUTTON");
        mobilePage.click("API_DEMOS.VIEWS.BUTTONS.SMALL_BUTTON");
        softAssert.assertTrue(mobilePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.OFF"));
        mobilePage.click("API_DEMOS.VIEWS.BUTTONS.TOGGLE_BUTTON");
        softAssert.assertFalse(mobilePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.OFF"));
        softAssert.assertTrue(mobilePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.ON"));
        mobilePage.click("API_DEMOS.VIEWS.BUTTONS.TOGGLE_BUTTON");
        softAssert.assertFalse(mobilePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.ON"));
        softAssert.assertTrue(mobilePage.isElementVisible("API_DEMOS.VIEWS.BUTTONS.OFF"));

/*        ViewsButtonsPage viewsButtonsPage = new ViewsButtonsPage();
        softAssert.assertTrue(viewsButtonsPage.checkToggleButtonState("OFF"));
        viewsButtonsPage.tapToggleButton();
        softAssert.assertTrue(viewsButtonsPage.checkToggleButtonState("ON"));*/

        softAssert.assertAll();
    }

}
