package org.apidemos;

import org.apidemos.base.BasePage;
import org.apidemos.base.TouchAction;
import org.testng.annotations.Test;

//@Listeners(TestListener.class)
public class GestureTest extends BaseTest{

    @Test
    public void swipeTest() {

        TouchAction touchAction = new TouchAction(driver);

        BasePage basePage = new BasePage();
        basePage.waitInSeconds(4);
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
