package org.apidemos.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.apidemos.base.BasePage;
import org.openqa.selenium.WebElement;

public class ViewsButtonsPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.Button[@text='NORMAL']")
    private WebElement normalBtn;

    @AndroidFindBy(xpath = "//android.widget.Button[@text='SMALL']")
    private WebElement smallBtn;

    @AndroidFindBy(xpath = "//android.widget.ToggleButton")
    private WebElement toggleBtn;



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