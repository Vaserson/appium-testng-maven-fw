package org.apidemos.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apidemos.base.BasePage;
import org.openqa.selenium.By;

import static io.appium.java_client.AppiumBy.xpath;

/**
 * Represents the Views Buttons page and provides methods to interact with its elements.
 */
public class ViewsButtonsPage extends BasePage {

    private static final Logger LOGGER = LogManager.getLogger(ViewsButtonsPage.class);

    private final By normalBtn = xpath("//android.widget.Button[@text='NORMAL']");
    private final By smallBtn = xpath("//android.widget.Button[@text='SMALL']");
    private final By toggleBtn = xpath("//android.widget.ToggleButton");

    private final String btnOnImg = "images/btn_on.png";
    private final String btnOffImg = "images/btn_off.png";

    /**
     * Clicks the toggle button on the page.
     *
     * @return the current instance of ViewsButtonsPage.
     */
    public ViewsButtonsPage tapToggleButton() {
        LOGGER.info("Clicking the toggle button.");
        click(toggleBtn);
        return this;
    }

    /**
     * Retrieves the text from the toggle button.
     *
     * @return the text of the toggle button.
     */
    private String getToggleButtonText() {
        String text = getText(toggleBtn);
        LOGGER.info("Toggle button text: {}", text);
        return text;
    }

    /**
     * Validates the state of the toggle button based on its text and image.
     *
     * @param state The expected state of the button ("ON" or "OFF").
     * @return true if the state matches the expected state and image; otherwise, false.
     */
    public boolean checkToggleButtonState(String state) {
        LOGGER.info("Checking toggle button state: {}", state);

        switch (state.toUpperCase()) {
            case "ON" -> {
                boolean isOn = getToggleButtonText().equalsIgnoreCase("ON")
                        && findElementByImage(btnOnImg).isDisplayed();
                LOGGER.info("Toggle button is in ON state: {}", isOn);
                return isOn;
            }
            case "OFF" -> {
                boolean isOff = getToggleButtonText().equalsIgnoreCase("OFF")
                        && findElementByImage(btnOffImg).isDisplayed();
                LOGGER.info("Toggle button is in OFF state: {}", isOff);
                return isOff;
            }
            default -> {
                LOGGER.error("Unsupported toggle button state: {}", state);
                throw new IllegalArgumentException("Unsupported state: " + state);
            }
        }
    }
}
