package org.app.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScreenUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenUtils.class);

    private static Point screenCenter;

    public static Point getScreenCenter(AppiumDriver driver) {
        if (screenCenter == null) {
            Dimension screenSize = driver.manage().window().getSize();
            screenCenter = new Point(screenSize.getWidth() / 2, screenSize.getHeight() / 2);
        }
        return screenCenter;
    }
}