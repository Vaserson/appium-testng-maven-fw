package org.apidemos.utils;

import org.apidemos.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestUtils {

    public static final long WAIT = 15;

    private TestUtils() {}

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    //TODO Makes 2 screenshots. Why?
    public static void getScreenshotOnFailedMethod(int testStatus, String methodName) {
        String imagePath = "Screenshots" + File.separator + TestUtils.getDateTime() + "_" + methodName + ".png";
        if (testStatus == 2) {
            try {
                File file = DriverFactory.getDriver().getScreenshotAs(OutputType.FILE);
                FileHandler.copy(file, new File(imagePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
