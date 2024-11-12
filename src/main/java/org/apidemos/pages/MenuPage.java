package org.apidemos.pages;

import org.apidemos.base.BasePage;

public class MenuPage extends BasePage {

    public MenuPage openMenu(String menuName) {
        click(findByAccessibilityId(menuName));
        return this;
    }
}