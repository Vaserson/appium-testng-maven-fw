package org.app.pages.apidemos;

import io.appium.java_client.AppiumDriver;
import org.app.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.appium.java_client.AppiumBy.xpath;

/**
 * Represents the Views Buttons page and provides methods to interact with its elements.
 */
public class ViewsButtonsPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewsButtonsPage.class);

    private final By normalBtn = xpath("//android.widget.Button[@text='NORMAL']");
    private final By smallBtn = xpath("//android.widget.Button[@text='SMALL']");
    private final By toggleBtn = xpath("//android.widget.ToggleButton");

    private final String btnOnImg = "images/btn_on.png";
    private final String btnOffImg = "images/btn_off.png";

    public ViewsButtonsPage(AppiumDriver driver) {
        super(driver);
    }

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
        LOGGER.info("Toggle button text is [{}]", text);
        return text;
    }

    /**
     * Validates the state of the toggle button based on its text and image.
     *
     * @param state The expected state of the button ("ON" or "OFF").
     * @return true if the state matches the expected state and image; otherwise, false.
     */
    public boolean checkToggleButtonState(String state) {
        LOGGER.info("Button state is [{}]", state);

        switch (state.toUpperCase()) {
            case "ON" -> {
                boolean isOn = getToggleButtonText().equalsIgnoreCase("ON")
                        && findElementByImage(btnOnImg).isDisplayed();
                LOGGER.info("Toggle button is in ON state: [{}]", isOn);
                return isOn;
            }
            case "OFF" -> {
                boolean isOff = getToggleButtonText().equalsIgnoreCase("OFF")
                        && findElementByImage(btnOffImg).isDisplayed();
                LOGGER.info("Toggle button is in OFF state: [{}]", isOff);
                return isOff;
            }
            default -> {
                LOGGER.error("Unsupported toggle button state: [{}]", state);
                throw new IllegalArgumentException("Unsupported state: " + state);
            }
        }
    }
}
