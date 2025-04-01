package org.app.methods;

import org.app.base.MobilePage;
import org.app.pages.apidemos.MenuPage;
import org.testng.annotations.Test;

//@Listeners(TestListener.class)
public class GestureTest extends MethodsBaseTest {

    @Test
    public void swipeTest() {
        new MenuPage(driver)
                .openViewsMenu();

        MobilePage basePage = new MobilePage(driver);
        basePage.waitInSeconds(2);
        basePage.scroll("down", "scroll", 1);
        basePage.waitInSeconds(2);
        basePage.scroll("up", "scroll", 1);
        basePage.waitInSeconds(2);
        basePage.scroll("down", "swipe", 1);
        basePage.waitInSeconds(2);
        basePage.scroll("up", "swipe", 1);
        basePage.waitInSeconds(2);
        basePage.scroll("down", "flick", 1);
        basePage.waitInSeconds(2);
        basePage.scroll("up", "flick", 1);
        basePage.waitInSeconds(2);

    }

}
