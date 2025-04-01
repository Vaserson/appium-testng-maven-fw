package org.app.desktop.chrome.qalight;

import org.app.pages.web.google.SearchPage;
import org.app.pages.web.qalight.MainPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class NewsTest extends QalightBaseTest {
    
    @Test
    public void testOpeningFirstNews() {
        SoftAssert softAssert = new SoftAssert();
        
        // Test web app functionality
        new MainPage(driver)
                .openNewsPage()
                .clickFirstNewsBlock();

    }
} 