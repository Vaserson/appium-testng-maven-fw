package org.apidemos.base;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

public class TouchAction {
    private final AppiumDriver driver;
    private final PointerInput finger;

    private static final int DEFAULT_DISTANCE = 200;
    private static final int DEFAULT_DURATION = 500;

    public TouchAction(AppiumDriver driver) {
        this.driver = driver;
        this.finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    }

    public void tap(int x, int y) {
        Sequence tap = new Sequence(finger, 1);
        tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(tap));
    }

    public void tap(WebElement element) {
        int x = element.getRect().getX() + element.getRect().getWidth() / 2;
        int y = element.getRect().getY() + element.getRect().getHeight() / 2;
        tap(x, y);
    }


    public void doubleTap(int x, int y) {
        Sequence doubleTap = new Sequence(finger, 0);
        for (int i = 0; i < 2; i++) {
            doubleTap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                    .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()))
                    .addAction(new Pause(finger, Duration.ofMillis(100)));
        }

        driver.perform(List.of(doubleTap));
    }


    public void longPress(int x, int y, Duration duration) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");

        Sequence longPress = new Sequence(finger, 0);
        longPress.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(new Pause(finger, duration))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(longPress));
    }

    public void longPress(int x, int y, long millis) {
        longPress(x, y, Duration.ofMillis(millis));
    }

    public void longPress(WebElement element, Duration duration) {
        int x = element.getRect().getX() + element.getRect().getWidth() / 2;
        int y = element.getRect().getY() + element.getRect().getHeight() / 2;
        longPress(x, y, duration);
    }

    public void longPress(WebElement element, long millis) {
        longPress(element, Duration.ofMillis(millis));
    }


    public void swipe(int startX, int startY, int endX, int endY, Duration duration) {
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipe));
    }

    public void swipe(int startX, int startY, int endX, int endY, long millis) {
        swipe(startX, startY, endX, endY, Duration.ofMillis(millis));
    }


    public void dragAndDrop(int startX, int startY, int endX, int endY, Duration duration) {
        Sequence dragAndDrop = new Sequence(finger, 0);
        dragAndDrop.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), endX, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(dragAndDrop));
    }

    public void dragAndDrop(int startX, int startY, int endX, int endY, long millis) {
        dragAndDrop(startX, startY, endX, endY, Duration.ofMillis(millis));
    }

    public void dragAndDrop(WebElement source, WebElement target, Duration duration) {
        int startX = source.getRect().getX() + source.getRect().getWidth() / 2;
        int startY = source.getRect().getY() + source.getRect().getHeight() / 2;

        int endX = target.getRect().getX() + target.getRect().getWidth() / 2;
        int endY = target.getRect().getY() + target.getRect().getHeight() / 2;

        dragAndDrop(startX, startY, endX, endY, duration);
    }

    public void dragAndDrop(WebElement source, WebElement target, long millis) {
        dragAndDrop(source, target, Duration.ofMillis(millis));
    }

    public void dragAndDrop(WebElement source, int offsetX, int offsetY, Duration duration) {
        int startX = source.getRect().getX() + source.getRect().getWidth() / 2;
        int startY = source.getRect().getY() + source.getRect().getHeight() / 2;

        int endX = startX + offsetX;
        int endY = startY + offsetY;

        dragAndDrop(startX, startY, endX, endY, duration);
    }

    public void dragAndDrop(WebElement source, int offsetX, int offsetY, long millis) {
        dragAndDrop(source, offsetX, offsetY, Duration.ofMillis(millis));
    }


    public void zoom(int centerX, int centerY, int distance, Duration duration) {
        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        Sequence zoom = new Sequence(finger1, 0);
        zoom.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY))
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger1.createPointerMove(duration, PointerInput.Origin.viewport(), centerX - distance, centerY))
                .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence zoom2 = new Sequence(finger2, 1);
        zoom2.addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX, centerY))
                .addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger2.createPointerMove(duration, PointerInput.Origin.viewport(), centerX + distance, centerY))
                .addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(zoom, zoom2));
    }

    public void zoom(int centerX, int centerY, int distance, long millis) {
        zoom(centerX, centerY, distance, Duration.ofMillis(millis));
    }

    public void zoomOnElement(WebElement element, int distance, Duration duration) {
        int centerX = element.getRect().getX() + (element.getRect().getWidth() / 2);
        int centerY = element.getRect().getY() + (element.getRect().getHeight() / 2);

        zoom(centerX, centerY, distance, duration);
    }

    public void zoomOnElement(WebElement element, int distance, long millis) {
        zoomOnElement(element, distance, Duration.ofMillis(millis));
    }

    public void zoomFromCenter() {
        int screenWidth = driver.manage().window().getSize().getWidth();
        int screenHeight = driver.manage().window().getSize().getHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        zoom(centerX, centerY, DEFAULT_DISTANCE, DEFAULT_DURATION);
    }


    public void pinch(int centerX, int centerY, int distance, Duration duration) {
        PointerInput finger1 = new PointerInput(PointerInput.Kind.TOUCH, "finger1");
        PointerInput finger2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

        Sequence pinch = new Sequence(finger1, 0);
        pinch.addAction(finger1.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX - distance, centerY))
                .addAction(finger1.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger1.createPointerMove(duration, PointerInput.Origin.viewport(), centerX, centerY))
                .addAction(finger1.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence pinch2 = new Sequence(finger2, 1);
        pinch2.addAction(finger2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), centerX + distance, centerY))
                .addAction(finger2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger2.createPointerMove(duration, PointerInput.Origin.viewport(), centerX, centerY))
                .addAction(finger2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(List.of(pinch, pinch2));
    }

    public void pinch(int centerX, int centerY, int distance, long millis) {
        pinch(centerX, centerY, distance, Duration.ofMillis(millis));
    }

    public void pinchOnElement(WebElement element, int distance, Duration duration) {
        int centerX = element.getRect().getX() + (element.getRect().getWidth() / 2);
        int centerY = element.getRect().getY() + (element.getRect().getHeight() / 2);

        pinch(centerX, centerY, distance, duration);
    }

    public void pinchOnElement(WebElement element, int distance, long millis) {
        pinchOnElement(element, distance, Duration.ofMillis(millis));
    }

    public void pinchFromCenter() {
        int screenWidth = driver.manage().window().getSize().getWidth();
        int screenHeight = driver.manage().window().getSize().getHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        pinch(centerX, centerY, DEFAULT_DISTANCE, DEFAULT_DURATION);
    }

}