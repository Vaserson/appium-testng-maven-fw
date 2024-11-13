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
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BasePage {
    private static final Logger LOGGER = LogManager.getLogger(BasePage.class);
    protected static String dateTime;
    protected AppiumDriver driver;

    public BasePage() {
        this.driver = DriverFactory.getDriver();
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

    public WebElement findElementByDynamicText(String text) {
        LOGGER.info("Looking for an element with text: {}", text);
        return driver.findElement(AppiumBy.xpath("//*[@text='" + text + "']"));
    }

    public WebElement findElementByDynamicXpath(String xpath) {
        LOGGER.info("Looking for an element with xpath: {}", xpath);
        return driver.findElement(AppiumBy.xpath(xpath));
    }

    public WebElement findByAccessibilityId(String accessibilityId) {
        LOGGER.info("Looking for an element with accessibilityId: {}", accessibilityId);
        return driver.findElement(AppiumBy.accessibilityId(accessibilityId));
    }

    public boolean isElementVisible(WebElement element, int timeToWait) {
        LOGGER.info("Checking visibility of element: {}", element);
        FluentWait<AppiumDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeToWait))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
        try {
            LOGGER.info("Waiting for element visibility for up to {} seconds", timeToWait);
            boolean isVisible = !fluentWait.until(ExpectedConditions.visibilityOfAllElements(element)).isEmpty();
            LOGGER.info("Element is visible: {}", isVisible);
            return isVisible;
        } catch (TimeoutException e) {
            LOGGER.warn("Element was not visible within {} seconds", timeToWait);
            return false;
        }
    }

    public void waitForVisibility(WebElement element) {
        LOGGER.info("Waiting for {} for default {} seconds", element, TestUtils.WAIT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        wait.until(ExpectedConditions.visibilityOf(element));
        LOGGER.info("Element: {} is found", element);
    }

    public WebElement waitForVisibility(By element) {
        LOGGER.info("Waiting for {} for default {} seconds", element, TestUtils.WAIT);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TestUtils.WAIT));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(element));
//        LOGGER.info("Element: {} is found", element);
    }

    public void waitForVisibility(WebElement element, int timeToWait) {
        LOGGER.info("Waiting for {} for {} seconds", element, timeToWait);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeToWait));
        wait.until(ExpectedConditions.visibilityOf(element));
        LOGGER.info("Element: {} is found", element);
    }

    public boolean waitForInvisibility(WebElement element, int timeToWait) {
        LOGGER.info("Waiting for invisibility of {} for {} seconds", element, timeToWait);
        FluentWait<AppiumDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeToWait))
                .pollingEvery(Duration.ofSeconds(1));
        try {
            boolean isInvisible = fluentWait.until(ExpectedConditions.invisibilityOfAllElements(element));
            LOGGER.info("Element: {} is now invisible: {}", element, isInvisible);
            return isInvisible;
        } catch (TimeoutException e) {
            LOGGER.warn("Element: {} was not invisible within {} seconds", element, timeToWait);
            return false;
        }
    }

    public void click(WebElement element) {
        waitForVisibility(element);
        LOGGER.info("Clicking element {}", element);
        element.click();
    }

    public void click(By element) {
        WebElement el = waitForVisibility(element);
        LOGGER.info("Clicking element {}", element);
        el.click();
    }

    public void click(WebElement element, int timeToWait) {
        waitForVisibility(element, timeToWait);
        LOGGER.info("Trying to click an element {} for {} seconds", element, timeToWait);
        element.click();
    }

    public void click(WebElement e1, WebElement e2) {
        try {
            LOGGER.info("Trying to click the first of two element {}", e1);
            click(e1, 2);
        } catch (Exception ex1) {
            try {
                LOGGER.info("Trying to click the second of two element {}", e2);
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
                LOGGER.info("Trying to click: {}", element);
                tries -= 1;
                element.click();
            }
        } catch (NoSuchElementException e) {
            LOGGER.info("No elements {} left to be clicked", element);
        }
    }

    public void clickWhileExist(WebElement element, int counter) {
        LOGGER.debug(element);
        int tries = 0;
        try {
            while (tries != counter) {
                LOGGER.info("Trying to click: {}", element);
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
                LOGGER.info("Trying to click: {}", element);
                element.click();
            }
        } catch (NoSuchElementException e) {
            LOGGER.info("{} is clicked", element);
        }
    }

    public String getAttribute(WebElement element, String attr) {
        waitForVisibility(element);
        LOGGER.info("Getting attribute {} from element {}", attr, element);
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

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), xCenter, yCenter))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }

    public void clear(WebElement e) {
        waitForVisibility(e);
        e.clear();
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

    public void scrollByScreen(String direction) {
        LOGGER.info("Scroll screen {}", direction);
        scrollByScreenTimes(direction, 1);
    }

    public void scrollToElementWhileAnotherElement(WebElement element, WebElement anotherElement, String direction) {
        int maxScrollAttempts = 10; // Define a maximum number of scroll attempts

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
                    scrollByScreen(direction);
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

    public void scrollByScreenTimes(String direction, int repeater) {
        Dimension dim = driver.manage().window().getSize();
        int x = dim.getWidth() / 2;
        int startY = 0;
        int endY = 0;

        if (direction.equalsIgnoreCase("up")) {
            startY = (int) (dim.getHeight() * 0.2);
            endY = (int) (dim.getHeight() * 0.8);
        } else {
            startY = (int) (dim.getHeight() * 0.8);
            endY = (int) (dim.getHeight() * 0.2);
        }

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        for (int i = 0; i < repeater; i++) {
            swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, startY));
            swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            swipe.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), x, endY));
            swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        }
        driver.perform(List.of(swipe));
    }

    public void scrollToElement(WebElement element, String direction) {
        int limit = 10; // Default
        while (limit > 0) {
            try {
                element.isDisplayed();
                break;
            } catch (NoSuchElementException e) {
                String beforeSwipe = driver.getPageSource();
                scrollByScreen(direction);
                waitInSeconds(4);
                String afterSwipe = driver.getPageSource();
                if (beforeSwipe.equals(afterSwipe)) {
                    throw new EndOfPageException("No element: " + element + " was found while scrolling and the end of the page is reached");
                }
                LOGGER.info("Attempts left: {} trying to scroll to element: {}", limit, element);
                limit--;
                LOGGER.info("NoSuchElementException caught");
            }
        }
    }

    public void scrollToElementByText(String text, String direction) {
        int limit = 5;
        while (driver.findElements(By.xpath("//*[@text='" + text + "']")).isEmpty() && limit > 0) {
            scrollByScreen(direction);
            limit--;
        }
        if (limit == 0) {
            throw new SwipeLimitExceededException("No element with text " + text + " was found");
        }
    }

    public WebElement scrollToElementByAttributeAndValue(String attr, String value) {
        return driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector()." + attr + "(\"" + value + "\"))"));
    }

    public void waitInSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted state
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

    public void dragFromPointToPoint(PointOption start, PointOption finish) {
        TouchAction action = new TouchAction((AndroidDriver)driver);

        action.press(start);
        action.waitAction(WaitOptions.waitOptions(Duration.ofMillis(100)));
        action.moveTo(finish);
        action.perform();
    }

    public void manageNotifications(Boolean show) {
        Dimension screenSize = getScreenSize();
        int yMargin = 3;
        int xMid = screenSize.width / 2;
        PointOption top = PointOption.point(xMid, yMargin);
        PointOption bottom = PointOption.point(xMid, screenSize.height - yMargin);

        if(show) {
            dragFromPointToPoint(top, bottom);
        } else {
            dragFromPointToPoint(bottom, top);
        }
    }

    public Dimension getScreenSize() {
        return driver.manage().window().getSize();
    }

    public void pressHomeButton() {
        ((AndroidDriver)driver).pressKey(new KeyEvent(AndroidKey.HOME));
    }


}
