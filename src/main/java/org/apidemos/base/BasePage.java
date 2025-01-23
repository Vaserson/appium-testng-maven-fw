package org.apidemos.base;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apidemos.driver.DriverFactory;
import org.apidemos.exceptions.*;
import org.apidemos.utils.FileUtils;
import org.apidemos.utils.PlatformUtils;
import org.apidemos.utils.TestUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

public class BasePage {
    private static final Logger LOGGER = LogManager.getLogger(BasePage.class);
    protected static String dateTime;
    protected AppiumDriver driver;
    protected TouchAction touchAction;


    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.touchAction = new TouchAction(driver);
    }


    public AppiumDriver getDriver() {
        return driver;
    }

    public void openApp(String appPackage, WebDriver driver) {
        LOGGER.info("Opening app with package name: [{}]", appPackage);
        try {
            ((AndroidDriver) driver).activateApp(appPackage);
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

    public void saveElementScreenshot(By locator, WebElement element) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImage = ImageIO.read(screenshot);

            BufferedImage elementImage = fullImage.getSubimage(
                    element.getRect().getX(), element.getRect().getY(),
                    element.getRect().getWidth(), element.getRect().getHeight()
            );

            String imagePath = String.format("%s%s%s.png",
                    FileUtils.createDirectoryIfNotExists("img_locators"), File.separator,
                    FileUtils.sanitizeFileName(getElementDescription(locator))
            );

            File outputImage = new File(imagePath);

            ImageIO.write(elementImage, "png", outputImage);
            LOGGER.info("Saved screenshot of element [{}] to [{}]", getElementDescription(locator), imagePath);
        } catch (IOException | NoSuchElementException e) {
            LOGGER.error("Failed to save screenshot of element [{}]: {}", getElementDescription(locator), e.getMessage());
        }
    }

    private WebElement waitForVisibility(By locator, long timeout) {
        LOGGER.info("Waiting for an element [{}] to become visible within {} seconds", getElementDescription(locator), timeout);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        WebElement element;
        try {
            element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            saveElementScreenshot(locator, element);
        } catch (TimeoutException e) {
            LOGGER.error("Element [{}] was NOT found within {} seconds: \n{}", getElementDescription(locator), timeout, e.getMessage());
            throw new ElementNotFoundException("Element not found within timeout: " + locator, e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error while waiting for element [{}]: \n{}", getElementDescription(locator), e.getMessage());
            throw new FrameworkException("Unexpected error while waiting for element", e);
        }
        return element;
    }

    private WebElement waitForVisibility(By locator) {
        return waitForVisibility(locator, TestUtils.WAIT);
    }

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

    public WebElement findElementByDynamicText(String text) {
        WebElement element = waitForVisibility(AppiumBy.xpath("//*[@text='" + text + "']"));
        LOGGER.info("Element with text [{}] was found", text);
        return element;
    }

    public WebElement findElementByDynamicXpath(String xpath) {
        WebElement element = waitForVisibility(AppiumBy.xpath(xpath));
        LOGGER.info("Element with XPath [{}] was found", xpath);
        return element;
    }

    public WebElement findByAccessibilityId(String accessibilityId) {
        WebElement element = waitForVisibility(AppiumBy.accessibilityId(accessibilityId));
        LOGGER.info("Element with accessibility ID [{}] was found", accessibilityId);
        return element;
    }

    public void click(WebElement element) {
        LOGGER.info("Clicking element [{}]", getElementDescription(element));
        element.click();
    }

    public void click(By locator, long timeout) {
        WebElement element = waitForVisibility(locator, timeout);
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
            WebElement element = waitForVisibility(locator, 5);
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
                element = waitForVisibility(locator); // Wait for visibility before proceeding
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

    public String getAttribute(By locator, String attr) {
        WebElement element = waitForVisibility(locator);
        LOGGER.info("Getting attribute [{}] from element [{}]", attr, getElementDescription(locator));
        String attribute = element.getDomAttribute(attr);
        LOGGER.info("Attribute [{}] is [{}]", attr, attribute);

        return attribute;
    }

    public Rectangle getCoordinates(By locator) {
        WebElement element = waitForVisibility(locator);
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
        touchAction.tap(waitForVisibility(locator));
    }

    public void clear(By locator) {
        WebElement element = waitForVisibility(locator);
        LOGGER.info("Clearing element [{}]", getElementDescription(locator));
        element.clear();
    }

    public void sendKeys(By locator, String txt) {
        WebElement element = waitForVisibility(locator);
        LOGGER.info("Sending keys: [{}] to element [{}]", txt, getElementDescription(locator));
        element.sendKeys(txt);
    }

    public String getText(By locator) {
        LOGGER.info("Getting text attribute of element [{}]", getElementDescription(locator));
        String platform = PlatformUtils.getPlatform();
        return switch (platform) {
            case "ANDROID" -> getAttribute(locator, "text");
            case "IOS" -> getAttribute(locator, "label");
            default -> throw new IllegalStateException("Unsupported platform: " + platform);
        };
    }

    public void scroll(String direction) {
        LOGGER.info("Scroll screen {}", direction);
        scroll(direction, "scroll", 1);
    }

    public void scrollToElementWhileAnotherElement(By locator, By anotherLocator, String direction) {
        LOGGER.info("Scrolling {} to element [{}] while another element [{}]", direction, getElementDescription(locator), getElementDescription(anotherLocator));
        int attempt = 0;

        while (attempt < TestUtils.RETRY) {
            try {
                WebElement element = waitForVisibility(locator);
                if (element.isDisplayed() || element.isEnabled()) {
                    break;
                }
            } catch (NoSuchElementException e1) {
                try {
                    WebElement anotherElement = waitForVisibility(anotherLocator);
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

    public WebElement scrollToElement(By locator, String direction) {
        LOGGER.info("Attempting to scroll {} to an element with locator [{}]", direction, locator);
        int retry = 0;
        while (retry < TestUtils.RETRY) {
            try {
                WebElement element = waitForVisibility(locator);
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

    public void waitInSeconds(int seconds) {
        LOGGER.info("Waiting for {} seconds", seconds);
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WaitInSecondsException("Thread was interrupted while waiting", e);
        }
    }

    public String getDateTime() {
        LOGGER.info("Got date and time [{}]", (dateTime = TestUtils.getDateTime()));
        return dateTime;
    }

    public void dragFromPointToPoint(int xStart, int yStart, int xFinish, int yFinish) {
        LOGGER.info("Drag from point [{},{}] to point [{},{}]", xStart, yStart, xFinish, yFinish);
        touchAction.dragAndDrop(xStart, yStart, xFinish, yFinish, 1000L);
    }

    public void manageNotifications(boolean show) {
        Dimension screenSize = getScreenSize();
        int yTop = 3;
        int xMid = screenSize.width / 2;
        int yBottom = screenSize.height - yTop;

        if (show) {
            LOGGER.info("Showing notifications");
            dragFromPointToPoint(xMid, yTop, xMid, yBottom);
        } else {
            LOGGER.info("Hiding notifications");
            dragFromPointToPoint(xMid, yBottom, xMid, yTop);
        }
    }

    private Dimension getScreenSize() {
        Dimension dimension = driver.manage().window().getSize();
        LOGGER.info("Getting screen dimensions [{}]", dimension);
        return driver.manage().window().getSize();
    }

    public void pressAndroidButton(AndroidKey androidKey) {
        LOGGER.info("Pressing Android key [{}]", androidKey);
        ((AndroidDriver) driver).pressKey(new KeyEvent(androidKey));
    }


    // IMAGE LOCATOR
    private String getReferenceImageB64(String imagePath) throws IOException {
        LOGGER.info("Getting reference image in path [{}]", imagePath);

        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(imagePath);

        if (resourceStream == null) {
            LOGGER.error("Resource not found [{}]", imagePath);
            throw new ImageFileNotFoundException("Resource not found: " + imagePath);
        }

        byte[] imageBytes = resourceStream.readAllBytes();
        resourceStream.close();

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public WebElement findElementByImage(String imagePath) {
        String base64Image;
        try {
            base64Image = getReferenceImageB64(imagePath);
        } catch (IOException e) {
            LOGGER.error("Image locator file was not found: [{}]", imagePath);
            throw new RuntimeException("Image locator file not found: " + imagePath, e);
        }
        By imageLocator = AppiumBy.image(base64Image);

        LOGGER.info("Looking for an element by image [{}] for default {} seconds", imagePath, TestUtils.WAIT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));

        WebElement element = null;
        try {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(imageLocator));
        } catch (TimeoutException e) {
            LOGGER.error("Element [{}] was not found within the timeout", imagePath);
        }
        LOGGER.debug("isDisplayed: {}", element.isDisplayed()); // true
        LOGGER.debug("getSize: {}", element.getSize()); // (71, 69)
        LOGGER.debug("getLocation: {}", element.getLocation()); // (300, 1528)
        LOGGER.debug("getAttribute visual: {}", element.getAttribute("visual"));
        LOGGER.debug("getAttribute score: {}", element.getAttribute("score"));
        //visual returns matched image as base64 data if getMatchedImageResult is true
        //score returns the similarity score as a float number in range [0.0, 1.0] since Appium 1.18.0

        return element;
    }
}
