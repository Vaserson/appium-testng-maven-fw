package org.apidemos.base;

import org.apidemos.driver.DriverFactory;
import org.apidemos.exceptions.EndOfPageException;
import org.apidemos.exceptions.NoElementOnAllowedPartException;
import org.apidemos.exceptions.SwipeLimitExceededException;
import org.apidemos.exceptions.WaitInSecondsException;
import org.apidemos.utils.PlatformUtils;
import org.apidemos.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    public void openApp(String appPackage, WebDriver driver) {
        LOGGER.info("Opening app: {}", appPackage);
        ((AndroidDriver) driver).activateApp(appPackage);
    }

    public void closeApp(String appPackage, WebDriver driver) {
        LOGGER.info("Closing app: {}", appPackage);
        ((AndroidDriver) driver).terminateApp(appPackage);
    }

    private String getElementDescription(By locator) {
        return locator.toString().replace("By.", "");
    }

    private String getElementDescription(WebElement element) {
        String description = element.toString();
        if (description.contains("->")) {
            return description.substring(description.indexOf("->") + 2).trim();
        }
        return description;
    }

    public WebElement findElementByDynamicText(String text) {
        LOGGER.info("Looking for an element with text: [{}]", text);
        return driver.findElement(AppiumBy.xpath("//*[@text='" + text + "']"));
    }

    public WebElement findElementByDynamicXpath(String xpath) {
        LOGGER.info("Looking for an element with xpath: [{}]", xpath);
        return driver.findElement(AppiumBy.xpath(xpath));
    }

    public WebElement findByAccessibilityId(String accessibilityId) {
        LOGGER.info("Looking for an element with accessibilityId: [{}]", accessibilityId);
        return driver.findElement(AppiumBy.accessibilityId(accessibilityId));
    }

    public boolean isElementVisible(WebElement element, int timeToWait) {
        LOGGER.info("Checking visibility of element: [{}]", getElementDescription(element));
        FluentWait<AppiumDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeToWait))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
        try {
            LOGGER.info("Waiting for element visibility for up to {} seconds", timeToWait);
            boolean isVisible = !fluentWait.until(ExpectedConditions.visibilityOfAllElements(element)).isEmpty();
            LOGGER.info("Element is {}", isVisible ? "visible" : "invisible");
            return isVisible;
        } catch (TimeoutException e) {
            LOGGER.warn("Element was not visible within {} seconds", timeToWait);
            return false;
        }
    }

    public void waitForVisibility(WebElement element) {
        LOGGER.info("Waiting for element [{}] for default {} seconds", getElementDescription(element), TestUtils.WAIT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.visibilityOf(element));
        LOGGER.info("Element [{}] is found", getElementDescription(element));
    }

    public WebElement waitForVisibility(By element) {
        LOGGER.info("Waiting for element [{}] for default {} seconds", getElementDescription(element), TestUtils.WAIT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(element));
//        LOGGER.info("Element: {} is found", element);
    }

    public void waitForVisibility(WebElement element, int timeToWait) {
        LOGGER.info("Waiting for [{}] for {} seconds", getElementDescription(element), timeToWait);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeToWait));
        wait.until(ExpectedConditions.visibilityOf(element));
        LOGGER.info("Element [{}] is found", getElementDescription(element));
    }

    public boolean waitForInvisibility(WebElement element, int timeToWait) {
        LOGGER.info("Waiting for invisibility of [{}] for {} seconds", getElementDescription(element), timeToWait);
        FluentWait<AppiumDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeToWait))
                .pollingEvery(Duration.ofSeconds(1));
        try {
            boolean isInvisible = fluentWait.until(ExpectedConditions.invisibilityOfAllElements(element));
            LOGGER.info("Element [{}] is {}", getElementDescription(element), isInvisible ? "invisible" : "visible");
            return isInvisible;
        } catch (TimeoutException e) {
            LOGGER.warn("Element [{}] did not become invisible within {} seconds", getElementDescription(element), timeToWait);
            return false;
        }
    }

    public void click(WebElement element) {
        waitForVisibility(element);
        LOGGER.info("Clicking element [{}]", getElementDescription(element));
        element.click();
    }

    public void click(By element) {
        WebElement el = waitForVisibility(element);
        LOGGER.info("Clicking element [{}]", getElementDescription(element));
        el.click();
    }

    public void click(WebElement element, int timeToWait) {
        waitForVisibility(element, timeToWait);
        LOGGER.info("Trying to click an element [{}] for {} seconds", getElementDescription(element), timeToWait);
        element.click();
    }

    public void click(WebElement e1, WebElement e2) {
        try {
            LOGGER.info("Trying to click the first of two elements [{}]", getElementDescription(e1));
            click(e1, 2);
        } catch (Exception ex1) {
            try {
                LOGGER.info("Trying to click the second of two elements [{}]", getElementDescription(e2));
                click(e2, 2);
            } catch (Exception ex2) {
                LOGGER.warn("Neither of the two elements was clicked");
                throw new NoSuchElementException("All elements for click cannot be found");
            }
        }
    }

    public void clickWhileExist(WebElement element) {
        LOGGER.debug(element);
        int tries = 5;
        try {
            while (element.isDisplayed() && tries > 0) {
                LOGGER.info("Clicking [{}] while exists", getElementDescription(element));
                tries -= 1;
                element.click();
            }
        } catch (NoSuchElementException e) {
            LOGGER.info("No elements [{}] left to be clicked", getElementDescription(element));
        }
    }

    public void clickWhileExist(WebElement element, int counter) {
        LOGGER.debug(element);
        int tries = 0;
        try {
            while (tries != counter) {
                LOGGER.info("Clicking [{}] while exists", getElementDescription(element));
                tries += 1;
                element.click();
            }
        } catch (NoSuchElementException e) {
            LOGGER.info("{} is clicked", element);
        }
    }

    //TODO implement "byID functionality,
    // remove .getName()
    // fix logging
    // add counter
    public void clickWhileExistById(WebElement element) {
        LOGGER.info(element.getClass().getName());
        try {
            while (driver.findElements(By.id(element.toString())) != null) {
                LOGGER.info("Clicking [{}] while exists", getElementDescription(element));
                element.click();
            }
        } catch (NoSuchElementException e) {
            LOGGER.info("{} is clicked", getElementDescription(element));
        }
    }

    public String getAttribute(WebElement element, String attr) {
        waitForVisibility(element);
        LOGGER.info("Getting attribute {} from element [{}]", attr, getElementDescription(element));
        String attribute = element.getAttribute(attr);
        LOGGER.info("{} = {}", attr, attribute);
        return attribute;
    }

    public void getCoordinatesAndClick(WebElement element) {
        waitForVisibility(element);

        int xCenter = element.getRect().x + (element.getSize().width / 2);
        LOGGER.info("xCenter = {}", xCenter);
        int yCenter = element.getRect().y + (element.getSize().height / 2);
        LOGGER.info("yCenter = {}", yCenter);

        touchAction.tap(xCenter, yCenter);
    }

    public void clear(WebElement element) {
        waitForVisibility(element);
        LOGGER.info("Clearing element [{}]", getElementDescription(element));
        element.clear();
    }

    public void clickIfExists(WebElement element) {
        try {
            waitForVisibility(element, 5);
            element.click();
        } catch (Exception e) {
            LOGGER.info("Looks like the element is not there");
        }
    }

    public void clickIfExists(WebElement element1, WebElement element2) {
        try {
            waitForVisibility(element1, 10);
            try {
                waitForVisibility(element2, 2);
                click(element2);
            } catch (Exception e) {
                LOGGER.info("no element for click");
            }
        } catch (Exception e) {
            LOGGER.error("general element cannot be found");
            throw new NoSuchElementException("general element cannot be found");
        }
    }

    public void sendKeys(WebElement e, String txt) {
        waitForVisibility(e);
        LOGGER.info("Sending keys: {} to element {}", txt, e);
        e.sendKeys(txt);
    }

    public String getText(WebElement element) {
        String platform = PlatformUtils.getPlatform();
        return switch (platform) {
            case "ANDROID" -> getAttribute(element, "text");
            case "IOS" -> getAttribute(element, "label");
            default -> throw new IllegalStateException("Unsupported platform: " + platform);
        };
    }

    public String getText(By element) {
        String platform = PlatformUtils.getPlatform();
        WebElement el = waitForVisibility(element);
        return switch (platform) {
            case "ANDROID" -> getAttribute(el, "text");
            case "IOS" -> getAttribute(el, "label");
            default -> throw new IllegalStateException("Unsupported platform: " + platform);
        };
    }

    public void scroll(String direction) {
        LOGGER.info("Scroll screen {}", direction);
        scroll(direction, "scroll", 1);
    }

    public void scrollToElementWhileAnotherElement(WebElement element, WebElement anotherElement, String direction) {
        int maxScrollAttempts = 10;

        while (maxScrollAttempts > 0) {
            try {
                if (element.isDisplayed() || element.isEnabled()) {
                    break;
                }
            } catch (NoSuchElementException e1) {
                try {
                    if (anotherElement.isDisplayed() || anotherElement.isEnabled()) {
                        throw new NoElementOnAllowedPartException("No element: " + element + " was found while scrolling and the examination element is reached");
                    }
                } catch (NoSuchElementException e2) {
                    String beforeSwipe = driver.getPageSource();
                    scroll(direction);
                    waitInSeconds(4);
                    String afterSwipe = driver.getPageSource();
                    if (beforeSwipe.equals(afterSwipe)) {
                        throw new EndOfPageException("No element: " + element + " was found while scrolling and the end of the page is reached");
                    }
                    maxScrollAttempts--;
                    LOGGER.info("Attempts left: {}", maxScrollAttempts);
                }
            }
        }
    }

    public void scroll(String direction, String type, int repeater) {
        LOGGER.info("{} screen {} {} times", type, direction, repeater);
        Dimension dim = driver.manage().window().getSize();
        int startX;;
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
            case ("scroll") -> {}
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
            default ->  throw new IllegalArgumentException("Unsupported type: " + type);
        }

        for (int i = 0; i < repeater; i++) {
            touchAction.swipe(startX, startY, endX, endY, duration);
        }
    }

    public WebElement scrollToElement(WebElement element, String direction) {
        LOGGER.info("Scrolling {} to an element: {}", direction, element);
        int limit = 10;
        while (limit > 0) {
            try {
                element.isDisplayed();
                return element;
            } catch (NoSuchElementException e) {
                scroll(direction);
                limit--;
            }
        }
        throw new EndOfPageException("No element: " + element + " was found while scrolling and the end of the page is reached");
    }

    public WebElement scrollToElementByText(String text, String direction) {
        LOGGER.info("Scrolling {} to an element with text: {}", direction, text);
        int limit = 5;
        while (limit > 0) {
            List<WebElement> elements = driver.findElements(By.xpath("//*[@text='" + text + "']"));
            if (!elements.isEmpty()) {
                return elements.getFirst();
            }
            scroll(direction);
            limit--;
        }
        throw new SwipeLimitExceededException("No element with text " + text + " was found");
    }

    public WebElement scrollToElementByAttributeAndValue(String attr, String value) {
        LOGGER.info("Scrolling to an element with attribute: {} with value: {}", attr, value);
        return driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector()." + attr + "(\"" + value + "\"))"));
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

    public boolean isElementVisible(WebElement element, Integer timeToWait) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeToWait));
        wait.until(ExpectedConditions.visibilityOf(element));
        return element != null;
    }

    public boolean isElementExists(WebElement element, long timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            wait.until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public AppiumDriver getDriver() {
        return driver;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void dragFromPointToPoint(int xStart, int yStart, int xFinish, int yFinish) {
        touchAction.dragAndDrop(xStart, yStart, xFinish, yFinish, 1000L);
    }

    public void manageNotifications(Boolean show) {
        Dimension screenSize = getScreenSize();
        int yTop = 3;
        int xMid = screenSize.width / 2;
        int yBottom = screenSize.height - yTop;

        if(show) {
            dragFromPointToPoint(xMid, yTop, xMid, yBottom);
        } else {
            dragFromPointToPoint(xMid, yBottom, xMid, yTop);
        }
    }

    private Dimension getScreenSize() {
        return driver.manage().window().getSize();
    }

    public void pressHomeButton() {
        ((AndroidDriver)driver).pressKey(new KeyEvent(AndroidKey.HOME));
    }




    // IMAGE LOCATOR
    private static String getReferenceImageB64(String imagePath) throws IOException {
        LOGGER.info("Getting reference image in path: {}", imagePath);

        File refImgFile = new File(imagePath);
        return Base64.getEncoder().encodeToString(Files.readAllBytes(refImgFile.toPath()));
    }

    public WebElement findElementByImage(String imagePath) {
            // Prepare base64 image for searching
        String base64Image = null;
        try {
            base64Image = getReferenceImageB64(imagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        By imageLocator = AppiumBy.image(base64Image);

            LOGGER.info("Looking for element by image {} for default {} seconds", imageLocator, TestUtils.WAIT);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));

            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(imageLocator));
            LOGGER.info("isDisplayed: {}", element.isDisplayed()); // true
            LOGGER.info("getSize: {}", element.getSize()); // (71, 69)
            LOGGER.info("getLocation: {}", element.getLocation()); // (300, 1528)
            LOGGER.info("getAttribute visual: {}", element.getAttribute("visual"));
            LOGGER.info("getAttribute score: {}", element.getAttribute("score"));
            //visual returns matched image as base64 data if getMatchedImageResult is true
            //score returns the similarity score as a float number in range [0.0, 1.0] sine Appium 1.18.0

        return element;
    }
}
