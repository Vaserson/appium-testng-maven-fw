package org.app.methods;

import org.app.base.MobilePage;
import org.testng.annotations.Test;

public class MethodTest extends MethodsBaseTest {

    @Test
    public void methodTest() {
        MobilePage basePage = new MobilePage(driver);
    }
}
