package org.apidemos.pages;

import static io.appium.java_client.AppiumBy.xpath;

import org.apidemos.base.BasePage;
import org.openqa.selenium.By;

import java.net.URL;
import java.util.Objects;

public class ViewsButtonsPage extends BasePage {

    private final By normalBtn = xpath("//android.widget.Button[@text='NORMAL']");
    private final By smallBtn = xpath("//android.widget.Button[@text='SMALL']");
    private final By toggleBtn = xpath("//android.widget.ToggleButton");

    //TODO Reduce image locator code
    private final String btnOnImg = Objects.requireNonNull(
            getClass().getClassLoader().getResource("images/btn_on.jpg"),
            "Resource btn_on.jpg not found"
    ).getPath();

    private final String btnOffImg = Objects.requireNonNull(
            getClass().getClassLoader().getResource("images/btn_off.jpg"),
            "Resource btn_off.jpg not found"
    ).getPath();


    public ViewsButtonsPage tapToggleButton() {
        click(toggleBtn);
        return this;
    }

    private String getToggleButtonText() {
        return getText(toggleBtn);
    }

    public boolean checkToggleButtonState(String state) {
        URL resource = getClass().getClassLoader().getResource("btn.on.jpg");
        System.out.println("Resource path: " + (resource != null ? resource.getPath() : "Not found"));

        switch (state) {
            case "ON" -> {
                return getToggleButtonText().equalsIgnoreCase("ON")
                        && findElementByImage(btnOnImg).isDisplayed();
            }
            case "OFF" -> {
                return getToggleButtonText().equalsIgnoreCase("OFF")
                        && findElementByImage(btnOffImg).isDisplayed();
            }
            default -> {
                throw new IllegalArgumentException("Unsupported state: " + state);
            }
        }
    }
}