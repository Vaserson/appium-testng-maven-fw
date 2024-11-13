package org.apidemos.pages;

import static io.appium.java_client.AppiumBy.xpath;
import org.apidemos.base.BasePage;
import org.openqa.selenium.By;

public class ViewsButtonsPage extends BasePage {

    private final By normalBtn = xpath("//android.widget.Button[@text='NORMAL']");
    private final By smallBtn = xpath("//android.widget.Button[@text='SMALL']");
    private final By toggleBtn = xpath("//android.widget.ToggleButton");



    public ViewsButtonsPage tapToggleButton() {
        click(toggleBtn);
        return this;
    }

    private String getToggleButtonText() {
        return getText(toggleBtn);
    }

    public boolean isToggleButtonOn() {
        return getToggleButtonText().equals("ON");
    }
}