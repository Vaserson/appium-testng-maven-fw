package org.app.desktop.chrome.google;

import org.app.pages.web.google.SearchPage;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DesktopWebTest extends WebBaseTest {
    
    @Test
    public void testDesktopWebScenario() {
        SoftAssert softAssert = new SoftAssert();
        
        // Test web app functionality
        SearchPage searchPage = new SearchPage(driver);
        String webAppTitle = searchPage.getPageTitle();
        softAssert.assertNotNull(webAppTitle, "Web app title should not be null");
        
        // Perform web action
        searchPage.performWebAction();
        
        // Verify page is responsive
        softAssert.assertTrue(searchPage.isPageResponsive(), "Page should be responsive");
        
        softAssert.assertAll();
    }
} 