package org.app.base;

import org.app.driver.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public abstract class BaseTest {
    protected static Map<String, String> strings;
    protected static String dateTime;

    @BeforeTest
    public void beforeTest() {
        dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    @AfterMethod
    public void afterMethod() {
        DriverManager.quitAll();
    }
} 