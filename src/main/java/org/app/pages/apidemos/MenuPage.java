package org.app.pages.apidemos;

import io.appium.java_client.android.nativekey.AndroidKey;
import org.app.base.BasePage;

public class MenuPage extends BasePage {

    public MenuPage openMenu(String menuName) {
        click(findElementByDynamicAccessibilityId(menuName));
        return this;
    }

    public MenuPage clickBackButton() {
        pressAndroidButton(AndroidKey.BACK);
        return this;
    }

    public MenuPage openAccess_ibilityMenu() {
        click(getLocator("API_DEMOS.ACCESS_IBILITY"));
        return this;
    }

    public MenuPage openAccessibilityMenu() {
        click(getLocator("API_DEMOS.ACCESSIBILITY"));
        return this;
    }

    public MenuPage openAnimationMenu() {
        click(getLocator("API_DEMOS.ANIMATION"));
        return this;
    }

    public MenuPage openAppMenu() {
        click(getLocator("API_DEMOS.APP"));
        return this;
    }

    public MenuPage openContentMenu() {
        click(getLocator("API_DEMOS.CONTENT"));
        return this;
    }

    public MenuPage openGraphicsMenu() {
        click(getLocator("API_DEMOS.GRAPHICS"));
        return this;
    }

    public MenuPage openMediaMenu() {
        click(getLocator("API_DEMOS.MEDIA"));
        return this;
    }

    public MenuPage openNfcMenu() {
        click(getLocator("API_DEMOS.NFC"));
        return this;
    }

    public MenuPage openOsMenu() {
        click(getLocator("API_DEMOS.OS"));
        return this;
    }

    public MenuPage openPreferenceMenu() {
        click(getLocator("API_DEMOS.PREFERENCE"));
        return this;
    }

    public MenuPage openTextMenu() {
        click(getLocator("API_DEMOS.TEXT"));
        return this;
    }

    public MenuPage openViewsMenu() {
        click(getLocator("API_DEMOS.VIEWS"));
        return this;
    }
}