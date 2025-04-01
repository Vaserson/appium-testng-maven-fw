package org.app.base;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import org.app.enums.Platform;
import org.app.exceptions.*;
import org.app.utils.*;
import org.openqa.selenium.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class MobilePage extends BasePage{
    private static final Logger LOGGER = LoggerFactory.getLogger(MobilePage.class);
    protected static String dateTime;
    protected WebDriver driver;
    protected TouchAction touchAction;

    public MobilePage(WebDriver driver) {
        super(driver);
        this.touchAction = new TouchAction((AppiumDriver) driver);
    }


    public void getCoordinatesAndClick(By locator) {
        touchAction.tap(waitForElementToBeVisible(locator));
    }


    // =====================================
    // SCROLL
    // =====================================
    public void scroll(String direction) {
        LOGGER.info("Scroll screen {}", direction);
        scroll(direction, "scroll", 1);
    }

    public void scrollToElementWhileAnotherElement(By locator, By anotherLocator, String direction) {
        LOGGER.info("Scrolling {} to element [{}] while another element [{}]", direction, getElementDescription(locator), getElementDescription(anotherLocator));
        int attempt = 0;

        while (attempt < TestUtils.RETRY) {
            try {
                WebElement element = waitForElementToBeVisible(locator);
                if (element.isDisplayed() || element.isEnabled()) {
                    break;
                }
            } catch (NoSuchElementException e1) {
                try {
                    WebElement anotherElement = waitForElementToBeVisible(anotherLocator);
                    if (anotherElement.isDisplayed() || anotherElement.isEnabled()) {
                        throw new NoElementOnAllowedPartException("No element: " + getElementDescription(locator) + " was found while scrolling and the examination element is reached");
                    }
                } catch (NoSuchElementException e2) {
                    String beforeSwipe = driver.getPageSource();
                    scroll(direction);
                    waitInSeconds(4);
                    String afterSwipe = driver.getPageSource();
                    if (beforeSwipe.equals(afterSwipe)) {
                        throw new EndOfPageException("No element: " + locator + " was found while scrolling and the end of the page is reached");
                    }
                    attempt++;
                    LOGGER.info("Attempts left: {}", TestUtils.RETRY - attempt);
                }
            }
        }
    }

    public WebElement scrollToElement(String patch, String direction) {
        LOGGER.info("Attempting to scroll {} to an element [{}]", direction, getElementDescription(patch));
        int retry = 0;
        while (retry < TestUtils.RETRY) {
            try {
                WebElement element = waitForElementToBeVisible(patch);
                if (element.isDisplayed()) {
                    LOGGER.info("Element [{}] found after {} scroll attempts", getElementDescription(patch), retry + 1);
                    return element;
                }
            } catch (NoSuchElementException | ElementNotFoundException | TimeoutException e) {
                LOGGER.info("Element [{}] was not found on attempt {}/{}. Retrying scroll...", getElementDescription(patch), retry + 1, TestUtils.RETRY);
                scroll(direction);
                retry++;
            }
        }
        throw new SwipeLimitExceededException("Element w: " + getElementDescription(patch) + " was not found after " + TestUtils.RETRY + " attempts.");
    }

    public WebElement scrollToElement(String patch, String direction, int retries) {
        LOGGER.info("Attempting to scroll {} to an element [{}]", direction, getElementDescription(patch));
        int retry = 0;
        while (retry < retries) {
            try {
                WebElement element = waitForElementToBeVisible(patch, 0);
                if (element.isDisplayed()) {
                    LOGGER.info("Element [{}] found after {} scroll attempts", getElementDescription(patch), retry + 1);
                    return element;
                }
            } catch (NoSuchElementException | ElementNotFoundException | TimeoutException e) {
                LOGGER.info("Element [{}] was not found on attempt {}/{}. Retrying scroll...", getElementDescription(patch), retry + 1, retries);
                scroll(direction);
                retry++;
            }
        }
        throw new SwipeLimitExceededException("Element w: " + getElementDescription(patch) + " was not found after " + TestUtils.RETRY + " attempts.");
    }

    public WebElement scrollToElement(By locator, String direction) {
        LOGGER.info("Attempting to scroll {} to an element with locator [{}]", direction, locator);
        int retry = 0;
        while (retry < TestUtils.RETRY) {
            try {
                WebElement element = waitForElementToBeVisible(locator);
                if (element.isDisplayed()) {
                    LOGGER.info("Element with locator [{}] found after {} scroll attempts", locator, retry + 1);
                    return element;
                }
            } catch (NoSuchElementException e) {
                LOGGER.info("Element with locator [{}] not found on attempt {}/{}. Retrying scroll...", locator, retry + 1, TestUtils.RETRY);
                scroll(direction);
                retry++;
            }
        }
        throw new SwipeLimitExceededException("Element with locator: " + getElementDescription(locator) + " was not found after " + TestUtils.RETRY + " attempts.");
    }

    public WebElement scrollToElementByText(String text, String direction) {
        LOGGER.info("Scrolling {} to an element with text [{}]", direction, text);
        int retry = 0;
        while (retry < TestUtils.RETRY) {
            List<WebElement> elements = driver.findElements(By.xpath("//*[@text='" + text + "']"));
            if (!elements.isEmpty()) {
                return elements.getFirst();
            }
            scroll(direction);
            retry++;
        }
        throw new SwipeLimitExceededException("No element with text [" + text + "] was found while swiping retry limit was reached");
    }

    public WebElement scrollToElementByAttributeAndValue(String attr, String value) {
        LOGGER.info("Scrolling to an element with attribute [{}] and value [{}]", attr, value);
        try {
            return driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))" +
                            ".scrollIntoView(new UiSelector()." + attr + "(\"" + value + "\"))"));
        } catch (NoSuchElementException e) {
            LOGGER.error("Element with attribute [{}] and value [{}] not found after scrolling", attr, value);
            throw new EndOfPageException("No attribute [" + attr + "] with value [" + value + "] was found while the end of the page is reached");
        }
    }


    public void scroll(String direction, String type, int repeater) {
        LOGGER.info("{} screen {} {} times", type, direction, repeater);
        Dimension dim = driver.manage().window().getSize();
        int startX;
        int startY;
        int endX;
        int endY;
        Duration duration = Duration.ofMillis(700);

        if (direction.equalsIgnoreCase("up")) {
            startX = dim.getWidth() / 2;
            endX = dim.getWidth() / 2;
            startY = (int) (dim.getHeight() * 0.2);
            endY = (int) (dim.getHeight() * 0.8);
        } else if (direction.equalsIgnoreCase("down")) {
            startX = dim.getWidth() / 2;
            endX = dim.getWidth() / 2;
            startY = (int) (dim.getHeight() * 0.8);
            endY = (int) (dim.getHeight() * 0.2);
        } else if (direction.equalsIgnoreCase("left")) {
            startX = (int) (dim.getWidth() * 0.2);
            endX = (int) (dim.getWidth() * 0.8);
            startY = dim.getHeight() / 2;
            endY = dim.getHeight() / 2;
        } else if (direction.equalsIgnoreCase("right")) {
            startX = (int) (dim.getWidth() * 0.8);
            endX = (int) (dim.getWidth() * 0.2);
            startY = dim.getHeight() / 2;
            endY = dim.getHeight() / 2;
        } else {
            throw new IllegalArgumentException();
        }

        switch (type) {
            case ("scroll") -> {
            }
            case ("swipe") -> {
                int temp = startX;
                startX = endX;
                endX = temp;
                duration = Duration.ofMillis(400);
            }
            case ("flick") -> {
                int temp = startX;
                startX = endX;
                endX = temp;
                duration = Duration.ofMillis(100);
            }
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        }

        for (int i = 0; i < repeater; i++) {
            touchAction.swipe(startX, startY, endX, endY, duration);
        }
    }


    // =====================================
    // Drag and Drop
    // =====================================
    public void dragFromPointToPoint(int xStart, int yStart, int xFinish, int yFinish, long durationMs) {
        LOGGER.info("Drag from point [{},{}] to point [{},{}]", xStart, yStart, xFinish, yFinish);
        touchAction.dragAndDrop(xStart, yStart, xFinish, yFinish, durationMs);
    }


    // =====================================
    // Manage mobile NOTIFICATION CENTER
    // =====================================
    public void manageNotifications(boolean show) {
        Dimension screenSize = getScreenSize();
        int yTop = 3;
        int xMid = screenSize.width / 2;
        int yBottom = screenSize.height - yTop;

        if (show) {
            LOGGER.info("Showing notifications");
            dragFromPointToPoint(xMid, yTop, xMid, yBottom, 100L);
        } else {
            LOGGER.info("Hiding notifications");
            dragFromPointToPoint(xMid, yBottom, xMid, yTop, 100L);
        }
    }


    // =====================================
    // Press physical BUTTONS
    // =====================================
    public void pressAndroidButton(AndroidKey androidKey) {
        LOGGER.info("Pressing Android key [{}]", androidKey);
        ((AndroidDriver) driver).pressKey(new KeyEvent(androidKey));
    }


    // ==================================
    // IMAGE LOCATOR
    // ==================================
    public WebElement findElementByImage(String patch) {
        List<WebElement> elements = findElementsByImage(patch);
        return elements.stream().findFirst().orElse(null);
    }

    public List<WebElement> findElementsByImage(String patch) {
        String base64Image;
        Map<String, String> locator;
        try {
            locator = LocatorUtils.getLocator(patch);
            if (locator.containsKey("image")) {
                patch = locator.get("image");
            }
        } catch (JsonLocatorKeyMissingException e) {
            LOGGER.error("JSON locator [{}] was not found. Trying to use directly as an image path", patch);
        }

        base64Image = getReferenceImageB64(patch);

        By imageLocator = AppiumBy.image(base64Image);

        LOGGER.info("Looking for elements by image [{}] for default {} seconds", patch, TestUtils.WAIT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));

        List<WebElement> elements = new ArrayList<>();
        try {
            List<WebElement> foundElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(imageLocator));
            elements.addAll(foundElements);
        } catch (TimeoutException e) {
            LOGGER.error("Elements [{}] were not found within the timeout", patch);
        }

        if (!elements.isEmpty()) {
            for (WebElement element : elements) {
                LOGGER.debug("Element size: {}", element.getSize());
                LOGGER.debug("Element location: {}", element.getLocation());
                LOGGER.debug("Element attribute 'visual': {}", getAttribute(element, "visual"));
                LOGGER.debug("Element attribute 'score': {}", getAttribute(element, "score"));
            }
        }
        return elements;
    }


    private String getReferenceImageB64(String imagePath) {
        LOGGER.info("Getting reference image in path [{}]", imagePath);

        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(imagePath);

        if (resourceStream == null) {
            LOGGER.error("Resource not found [{}]", imagePath);
            throw new ImageFileNotFoundException("Resource not found: " + imagePath);
        }
        byte[] imageBytes;
        try {
            imageBytes = resourceStream.readAllBytes();
            resourceStream.close();
        } catch (IOException e) {
            throw new ImageFileNotFoundException("Problem occurred while reading an image file in path: " + imagePath);
        }

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    //TODO Image caching
/*    private final Map<String, String> imageCache = new ConcurrentHashMap<>();

    private String getReferenceImageB64(String imagePath) {
        return imageCache.computeIfAbsent(imagePath, this::convertToBase64);
    }

    private String convertToBase64(String imagePath) {
        // Logic to read and encode image as base64
    }*/
}