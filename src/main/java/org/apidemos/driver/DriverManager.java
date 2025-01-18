package org.apidemos.driver;

import io.appium.java_client.AppiumDriver;

public class DriverManager {
    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(AppiumDriver driverInstance) {
        driver.set(driverInstance);
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}