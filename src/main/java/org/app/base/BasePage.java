package org.app.base;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import org.app.driver.DriverFactory;
import org.app.exceptions.*;
import org.app.utils.*;
import org.openqa.selenium.*;
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

public class BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);
    protected static String dateTime;
    protected AppiumDriver driver;
    protected TouchAction touchAction;

    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.touchAction = new TouchAction(driver);
    }


    // ==================================
    // Getting Locator from JSON
    // ==================================
    //TODO Think how to return List<WebElement> sometimes
    //TODO Add more strategies (textExact, textContains, xpathDynamic)
    //TODO Add text searching correct between different platforms (text for Android, value for iOS)
    //TODO make getLocator to return just found String value and pass it to some findElement method (use LocatorStrategy enum)
    //TODO Verify if next line is working correctly
    public By getLocator(Map<String, String> locator) {
        if (locator == null || locator.isEmpty()) {
            throw new JsonLocatorKeyMissingException("Locator map cannot be null or empty.");
        }
        return locator.entrySet().stream()
                .map(entry -> switch (entry.getKey()) {
                    case "xpath" -> AppiumBy.xpath(entry.getValue());
                    case "resourceId" -> AppiumBy.id(entry.getValue());
                    case "accessibilityId" -> AppiumBy.accessibilityId(entry.getValue());
                    case "text" -> AppiumBy.xpath("//*[contains(@text,'" + entry.getValue() + "')]");
                    case "image" -> AppiumBy.image(getReferenceImageB64(entry.getValue()));
                    default -> throw new UnsupportedLocatorTypeException("Unsupported locator type for key: " + entry.getKey());
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No valid locator key found for: " + locator.keySet()));
    }

    public Point getCoordinates(Map<String, String> locator) {
        if (locator == null || locator.isEmpty()) {
            throw new JsonLocatorKeyMissingException("Locator map cannot be null or empty.");
        }

        if (!locator.containsKey("coordinates")) {
            throw new IllegalArgumentException("No coordinate key found in the locator map.");
        }

        String coordinates = locator.get("coordinates");
        String[] parts = coordinates.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid coordinates format. Expected 'x,y' but got: " + coordinates);
        }

        try {
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            return new Point(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be integers: " + coordinates, e);
        }
    }



    // ==================================
    // DRIVER
    // ==================================
    public AppiumDriver getDriver() {
        return driver;
    }


    // ==================================
    // Open/Close App
    // ==================================
    public void openApp(String appPackage, WebDriver driver) {
        LOGGER.info("Opening app with package name: [{}]", appPackage);
        try {
            if (PlatformUtils.isAndroid()) {
                ((AndroidDriver) driver).activateApp(appPackage);
            } else if (PlatformUtils.isIOS()) {
                ((IOSDriver) driver).activateApp(appPackage);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to open app with package: [{}]", appPackage);
            throw new AppNotFoundException("App could not be opened: " + appPackage, e);
        }
    }

    public void closeApp(String appPackage, WebDriver driver) {
        LOGGER.info("Closing app with package name: [{}]", appPackage);
        try {
            ((AndroidDriver) driver).terminateApp(appPackage);
        } catch (Exception e) {
            LOGGER.error("Failed to close app with package: [{}]", appPackage);
            throw new AppNotFoundException("App could not be closed: " + appPackage, e);
        }
    }


    // ==================================
    // Getting Element description
    // ==================================
    private String getElementDescription(String patch) {
        return patch;
    }

    private String getElementDescription(By locator) {
        return locator.toString().replace("By.", "");
    }

    private String getElementDescription(WebElement element) {
        String description = element.toString();
        description = description.replaceAll("^\\[|]$", "");
        if (description.contains("->")) {
            return description.substring(description.indexOf("->") + 2).trim();
        }
        return description;
    }


    // =====================================
    // Saving found JSON locator screenshot
    // =====================================
    public void saveElementScreenshot(String patch, WebElement element) {
        boolean saveScreenshots = Boolean.parseBoolean(PropertyUtils.getProperty("saveElementScreenshots"));

        if (!saveScreenshots) {
            LOGGER.info("Saving element screenshots is DISABLED.");
            return;
        } else {
            LOGGER.info("Saving element screenshots is ENABLED.");
        }
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImage = ImageIO.read(screenshot);

            BufferedImage elementImage = fullImage.getSubimage(
                    element.getRect().getX(), element.getRect().getY(),
                    element.getRect().getWidth(), element.getRect().getHeight()
            );

            String locatorImagesDir = FileUtils.createDirectoryIfNotExists("locator_images");
            String appLocatorImagesSubDir = FileUtils.createDirectoryIfNotExists(
                    locatorImagesDir + File.separator + PropertyUtils.getProperty("screenshotsBasePath"));
            String imagePath = String.format("%s%s%s.png",
                    appLocatorImagesSubDir, File.separator, patch);

            File outputImage = new File(imagePath);

            ImageIO.write(elementImage, "png", outputImage);
            LOGGER.info("Saved screenshot of element [{}] to [{}]", getElementDescription(patch), imagePath);
        } catch (IOException | NoSuchElementException e) {
            LOGGER.error("Failed to save screenshot of element [{}]: {}", getElementDescription(patch), e.getMessage());
        }
    }


    // =====================================
    // Wait for visibility of ONE element
    // =====================================
    private WebElement waitForElementToBeVisible(By locator, long timeout) {
        LOGGER.info("Waiting for an element [{}] to become visible within {} seconds", getElementDescription(locator), timeout);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        WebElement element;
        try {
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            LOGGER.error("Element [{}] was NOT found within {} seconds: \n{}", getElementDescription(locator), timeout, e.getMessage());
            throw new ElementNotFoundException("Element not found within timeout: " + locator, e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while waiting for element [{}]: \n{}", getElementDescription(locator), e.getMessage());
            throw new FrameworkException("Unexpected error while waiting for element", e);
        }
        return element;
    }

    private WebElement waitForElementToBeVisible(By locator) {
        return waitForElementToBeVisible(locator, TestUtils.WAIT);
    }

    private WebElement waitForElementToBeVisible(String patch, long timeout) {
        LOGGER.info("Waiting for an element [{}] to become visible within {} seconds", getElementDescription(patch), timeout);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        WebElement element;
        try {
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(LocatorUtils.getLocator(patch))));
            saveElementScreenshot(patch, element);
        } catch (TimeoutException e) {
            LOGGER.error("Element [{}] was NOT found within {} seconds: \n{}", getElementDescription(patch), timeout, e.getMessage());
            throw new ElementNotFoundException("Element not found within timeout: " + patch, e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while waiting for element [{}]: \n{}", getElementDescription(patch), e.getMessage());
            throw new FrameworkException("Unexpected error while waiting for element", e);
        }
        return element;
    }

    private WebElement waitForElementToBeVisible(String patch) {
        return waitForElementToBeVisible(patch, TestUtils.WAIT);
    }


    // =====================================
    // Wait for visibility of ALL elements
    // =====================================
    private List<WebElement> waitForElementsToBeVisible(By locator, long timeout) {
        LOGGER.info("Waiting for elements [{}] to become visible within {} seconds", getElementDescription(locator), timeout);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        List<WebElement> elements = List.of();
        try {
            elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            LOGGER.error("Elements [{}] were NOT found within {} seconds: \n{}", getElementDescription(locator), timeout, e.getMessage());
            return elements;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while waiting for elements [{}]: \n{}", getElementDescription(locator), e.getMessage());
            return elements;
        }
        return elements;
    }

    public List<WebElement> waitForElementsToBeVisible(By locator) {
        return waitForElementsToBeVisible(locator, TestUtils.WAIT);
    }

    public List<WebElement> waitForElementsToBeVisible(String patch, long timeout) {
        LOGGER.info("Waiting for elements [{}] to become visible within {} seconds", getElementDescription(patch), timeout);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        List<WebElement> elements = List.of();
        try {
            elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(getLocator(LocatorUtils.getLocator(patch))));

            //TODO Should we store first or all???
            saveElementScreenshot(patch, elements.getFirst());
        } catch (TimeoutException e) {
            LOGGER.error("Elements [{}] were NOT found within {} seconds: \n{}", getElementDescription(patch), timeout, e.getMessage());
            return elements;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while waiting for elements [{}]: \n{}", getElementDescription(patch), e.getMessage());
            return elements;
        }
        return elements;
    }

    public List<WebElement> waitForElementsToBeVisible(String patch) {
        return waitForElementsToBeVisible(patch, TestUtils.WAIT);
    }


    // =====================================
    // Verify if element VISIBLE
    // =====================================
    public boolean isElementVisible(String patch, long timeout) {
        LOGGER.info("Verifying if element [{}] is visible within {} seconds", getElementDescription(patch), timeout);
        return !waitForElementsToBeVisible(patch).isEmpty();
    }

    public boolean isElementVisible(String patch) {
        return isElementVisible(patch, TestUtils.WAIT);
    }


    // =====================================
    // Wait for INVISIBILITY
    // =====================================
    public boolean waitForInvisibility(By locator, int timeToWait) {
        LOGGER.info("Waiting for invisibility of [{}] for {} seconds", getElementDescription(locator), timeToWait);
        FluentWait<AppiumDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeToWait))
                .pollingEvery(Duration.ofSeconds(1));
        try {
            boolean isInvisible = fluentWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            LOGGER.info("Element with locator [{}] is {}", getElementDescription(locator), isInvisible ? "invisible" : "visible");
            return isInvisible;
        } catch (TimeoutException e) {
            LOGGER.warn("Element with locator [{}] did not become invisible within {} seconds", getElementDescription(locator), timeToWait);
            return false;
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred while waiting for invisibility of element with locator [{}]: \n{}", getElementDescription(locator), e.getMessage());
            return false;
        }
    }


    // =====================================
    // Find ELEMENT by PATCH
    // =====================================
    public WebElement findElement(String patch) {
        Map<String, String> locator = (LocatorUtils.getLocator(patch));
        if (locator.containsKey("image")) {
            return findElementByImage(locator.get("image"));
        }
        return findElementByLocator(locator);
    }

    private WebElement findElementByLocator(Map<String, String> locator) {
        if (locator == null || locator.isEmpty()) {
            throw new IllegalArgumentException("Locator map cannot be null or empty.");
        }
        By by = getLocator(locator);
        return waitForElementToBeVisible(by);
    }

    public List<WebElement> findElements(String patch) {
        Map<String, String> locator = LocatorUtils.getLocator(patch);
        if (locator.containsKey("image")) {
            return findElementsByImage(patch);
        }
        return findAllElementsByLocator(locator);
    }

    private List<WebElement> findAllElementsByLocator(Map<String, String> locator) {
        if (locator == null || locator.isEmpty()) {
            throw new IllegalArgumentException("Locator map cannot be null or empty.");
        }
        By by = getLocator(locator);
        return waitForElementsToBeVisible(by);
    }


    // =====================================
    // Find ELEMENT common
    // =====================================
    public WebElement findElementByDynamicText(String text) {
        //TODO Try WebDriverWait ExpectedConditions.textToBePresentInElement
/*        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));*/
        WebElement element = waitForElementToBeVisible(AppiumBy.xpath("//*[@text='" + text + "']"));
        LOGGER.info("Element with text [{}] was found", text);
        return element;
    }

    public WebElement findElementByDynamicXpath(String xpath) {
        WebElement element = waitForElementToBeVisible(AppiumBy.xpath(xpath));
        LOGGER.info("Element with XPath [{}] was found", xpath);
        return element;
    }

    public WebElement findElementByDynamicAccessibilityId(String accessibilityId) {
        WebElement element = waitForElementToBeVisible(AppiumBy.accessibilityId(accessibilityId));
        LOGGER.info("Element with accessibility ID [{}] was found", accessibilityId);
        return element;
    }


    // =====================================
    // CLICK
    // =====================================
    public void click(WebElement element) {
        LOGGER.info("Clicking element [{}]", getElementDescription(element));
        element.click();
    }

    public void click(String patch, long timeout) {
        LOGGER.info("Clicking element [{}]", getElementDescription(patch));
        click(waitForElementToBeVisible(patch, timeout));
    }

    public void click(String patch) {
        click(patch, TestUtils.WAIT);
    }

    public void click(By locator, long timeout) {
        LOGGER.info("Clicking element [{}]", getElementDescription(locator));
        WebElement element = waitForElementToBeVisible(locator, timeout);
        click(element);
    }

    public void click(By locator) {
        click(locator, TestUtils.WAIT);
    }

    public void click(By... locators) {
        for (By locator : locators) {
            String elementDescription = getElementDescription(locator);
            try {
                LOGGER.info("Trying to click element located by [{}]", elementDescription);
                click(locator, 2);
                return;
            } catch (Exception e) {
                LOGGER.warn("Failed to click element located by [{}]. Retrying with the next locator if available.", elementDescription);
            }
        }
        throw new NoSuchElementException("All locators provided for click cannot be found or interacted with.");
    }

    public void clickIfExist(By locator) {
        try {
            WebElement element = waitForElementToBeVisible(locator, 5);
            LOGGER.info("Element [{}] is visible and will be clicked", getElementDescription(locator));
            element.click();
        } catch (ElementNotFoundException e) {
            LOGGER.info("Element [{}] was not found for clicking", getElementDescription(locator));
        } catch (Exception e) {
            LOGGER.error("Unexpected error while clicking element [{}]", getElementDescription(locator), e);
        }
    }

    public void clickIfExist(By locator1, By locator2) {
        try {
            click(locator1, 10);
        } catch (Exception e1) {
            LOGGER.info("First of two elements located by [{}] wasn't found for click", getElementDescription(locator1));
            try {
                click(locator2, 2);
            } catch (Exception e2) {
                LOGGER.info("Second of two elements located by [{}] wasn't found for click", getElementDescription(locator2));
                throw new NoSuchElementException("Both elements were not found");
            }
        }
    }

    public void clickWhileExist(By locator, int counter) {
        LOGGER.debug("Checking element located by [{}] before clicking loop", getElementDescription(locator));
        int tries = 0;

        while (tries < counter) {
            WebElement element;

            try {
                element = waitForElementToBeVisible(locator); // Wait for visibility before proceeding
                if (element == null || !element.isDisplayed() || !element.isEnabled()) {
                    LOGGER.info("Element [{}] is not interactable after {} clicks", getElementDescription(locator), tries);
                    break;
                }
                LOGGER.info("Clicking [{}] (attempt {}/{})", getElementDescription(locator), tries + 1, counter);
                element.click();
                tries++;
            } catch (ElementNotFoundException e) {
                LOGGER.error("Element [{}] not found within timeout during attempt {}/{}. Breaking the loop.", getElementDescription(locator), tries + 1, counter);
                break;
            } catch (StaleElementReferenceException e) {
                LOGGER.warn("StaleElementReferenceException caught for element [{}] during attempt {}/{}. Retrying...", getElementDescription(locator), tries + 1, counter);
            } catch (NoSuchElementException e) {
                LOGGER.info("Element [{}] no longer exists after {} clicks", getElementDescription(locator), tries);
                break;
            } catch (Exception e) {
                LOGGER.error("Unexpected error while interacting with element [{}] during attempt {}/{}. Error: {}", getElementDescription(locator), tries + 1, counter, e.getMessage());
                break;
            }
        }
        if (tries == counter) {
            LOGGER.info("Completed {} clicks on element [{}] and it is still present", counter, getElementDescription(locator));
        }
    }

    public void clickWhileExist(By locator) {
        clickWhileExist(locator, TestUtils.RETRY);
    }


    // =====================================
    // Getting ATTRIBUTE
    // =====================================
    public String getAttribute(WebElement element, String attr) {
        LOGGER.info("Getting attribute [{}] from element [{}]", attr, getElementDescription(element));
        String attribute = element.getDomAttribute(attr);
        LOGGER.debug("Attribute for [{}] is [{}]", attr, attribute);
        return attribute;
    }

    public String getAttribute(By locator, String attr) {
        LOGGER.info("Getting attribute [{}] from element [{}]", attr, getElementDescription(locator));
        WebElement element = waitForElementToBeVisible(locator);
        String attribute = getAttribute(element, attr);
        LOGGER.debug("Attribute for [{}] is [{}]", attr, attribute);
        return attribute;
    }


    // =====================================
    // COORDINATES and DIMENSIONS
    // =====================================
    public Rectangle getCoordinates(By locator) {
        WebElement element = waitForElementToBeVisible(locator);
        LOGGER.info("Getting coordinates of element [{}]", getElementDescription(locator));

        int x1 = element.getRect().x;
        int y1 = element.getRect().y;
        int width = element.getSize().width;
        int height = element.getSize().height;
        int x2 = x1 + width;
        int y2 = y1 + height;

        LOGGER.info("Coordinates of element [{}]: Top-Left=({}, {}), Bottom-Right=({}, {})",
                getElementDescription(locator), x1, y1, x2, y2);

        return new Rectangle(x1, y1, height, width);
    }

    public void getCoordinatesAndClick(By locator) {
        touchAction.tap(waitForElementToBeVisible(locator));
    }

    private Dimension getScreenSize() {
        Dimension dimension = driver.manage().window().getSize();
        LOGGER.info("Getting screen dimensions [{}]", dimension);
        return driver.manage().window().getSize();
    }


    // =====================================
    // Send KEYS
    // =====================================
    public void clear(By locator) {
        WebElement element = waitForElementToBeVisible(locator);
        LOGGER.info("Clearing element [{}]", getElementDescription(locator));
        element.clear();
    }

    public void sendKeys(By locator, String txt) {
        WebElement element = waitForElementToBeVisible(locator);
        LOGGER.info("Sending keys: [{}] to element [{}]", txt, getElementDescription(locator));
        element.sendKeys(txt);
    }


    // =====================================
    // Getting TEXT
    // =====================================
    public String getText(String patch) {
        LOGGER.info("Getting text attribute of element [{}]", patch);
        Map<String, String> locator = LocatorUtils.getLocator(patch);
        By by = getLocator(locator);
        WebElement element = waitForElementToBeVisible(by);
        return getPlatformSpecificAttribute(element);
    }

    public String getText(By locator) {
        LOGGER.info("Getting text attribute of element [{}]", getElementDescription(locator));
        WebElement element = waitForElementToBeVisible(locator);
        return getPlatformSpecificAttribute(element);
    }

    public String getText(WebElement element) {
        LOGGER.info("Getting text attribute of element [{}]", getElementDescription(element));
        return getPlatformSpecificAttribute(element);
    }

    /**
     * Retrieves the platform-specific text attribute from the element.
     * @param element WebElement whose attribute needs to be fetched
     * @return text value for Android or iOS
     */
    private String getPlatformSpecificAttribute(WebElement element) {
        PlatformUtils.PlatformType platform = PlatformUtils.getPlatform();
        String attributeName = switch (platform) {
            case ANDROID -> "text";
            case IOS -> "label";
            default -> throw new IllegalStateException("Unsupported platform: " + platform.name());
        };
        return getAttribute(element, attributeName);
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


    // =====================================
    // Drag and Drop
    // =====================================
    public void dragFromPointToPoint(int xStart, int yStart, int xFinish, int yFinish, long durationMs) {
        LOGGER.info("Drag from point [{},{}] to point [{},{}]", xStart, yStart, xFinish, yFinish);
        touchAction.dragAndDrop(xStart, yStart, xFinish, yFinish, durationMs);
    }


    // =====================================
    // WAIT
    // =====================================
    public void waitInSeconds(int seconds) {
        LOGGER.info("Waiting for {} seconds", seconds);
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WaitInSecondsException("Thread was interrupted while waiting", e);
        }
    }


    // =====================================
    // Date and Time
    // =====================================
    public String getDateTime() {
        LOGGER.info("Got date and time [{}]", (dateTime = TestUtils.getDateTime()));
        return dateTime;
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
