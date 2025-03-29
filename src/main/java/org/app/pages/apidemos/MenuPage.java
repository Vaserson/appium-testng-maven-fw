package org.app.pages.apidemos;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import org.app.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MenuPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuPage.class);

    public MenuPage(AppiumDriver driver) {
        super(driver);
    }

    public MenuPage openMenu(String menuName) {
        click(findElementByDynamicAccessibilityId(menuName));
        return this;
    }

    public MenuPage clickBackButton() {
        pressAndroidButton(AndroidKey.BACK);
        return this;
    }

    public MenuPage openAccess_ibilityMenu() {
        click("API_DEMOS.ACCESS_IBILITY");
        return this;
    }

    public MenuPage openAccessibilityMenu() {
        click("API_DEMOS.ACCESSIBILITY");
        return this;
    }

    public MenuPage openAnimationMenu() {
        click("API_DEMOS.ANIMATION");
        return this;
    }

    public MenuPage openAppMenu() {
        click("API_DEMOS.APP");
        return this;
    }

    public MenuPage openContentMenu() {
        click("API_DEMOS.CONTENT");
        return this;
    }

    public MenuPage openGraphicsMenu() {
        click("API_DEMOS.GRAPHICS");
        return this;
    }

    public MenuPage openMediaMenu() {
        click("API_DEMOS.MEDIA");
        return this;
    }

    public MenuPage openNfcMenu() {
        click("API_DEMOS.NFC");
        return this;
    }

    public MenuPage openOsMenu() {
        click("API_DEMOS.OS");
        return this;
    }

    public MenuPage openPreferenceMenu() {
        click("API_DEMOS.PREFERENCE");
        return this;
    }

    public MenuPage openTextMenu() {
        click("API_DEMOS.TEXT");
        return this;
    }

    public MenuPage openViewsMenu() {
        click("API_DEMOS.VIEWS");
        return this;
    }
}