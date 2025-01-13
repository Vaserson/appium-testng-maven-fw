package org.apidemos.utils;

import io.appium.java_client.screenrecording.CanRecordScreen;
import org.apidemos.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class TestUtils {

    public static final long WAIT = 10;

    private TestUtils() {}

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    //TODO Makes 2 screenshots. Why? Because of TestListener is an additional approach
    public static void getScreenshotOnFailedMethod(int testStatus, String methodName) {
        if (testStatus != 2) {
            return;
        }
        String dir = "Screenshots";
        File screenshotsDir = new File(dir);

        if (!screenshotsDir.exists()) {
            screenshotsDir.mkdirs();
        }

        String imagePath = String.format("Screenshots%s%s_%s.png", File.separator, TestUtils.getDateTime(), methodName);

        try {
            File file = DriverFactory.getDriver().getScreenshotAs(OutputType.FILE);
            FileHandler.copy(file, new File(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }

    public static void stopVideoRecording(WebDriver driver, int testStatus, String testName) {
        String media = ((CanRecordScreen) driver).stopRecordingScreen();

        if (testStatus == 2) { // 2 => Only failed test methods

            String dir = "Videos";
            File videoDir = new File(dir);

            if (!videoDir.exists()) {
                videoDir.mkdirs();
            }

            try {
                FileOutputStream stream = new FileOutputStream(videoDir + File.separator + getDateTime() + "_" + testName + ".mp4");
                stream.write(Base64.getDecoder().decode(media));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
