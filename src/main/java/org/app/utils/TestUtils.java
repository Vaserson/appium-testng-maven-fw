package org.app.utils;

import io.appium.java_client.screenrecording.CanRecordScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.driver.DriverFactory;
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

public final class TestUtils {
    private static final Logger LOGGER = LogManager.getLogger(TestUtils.class);

    public static final long WAIT = 10;
    public static final int RETRY = 6;
    public static final int TEST_FAILED = 2;
    public static final int TEST_PASSED = 1;

    private TestUtils() {}

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void getScreenshotOnFailedMethod(int testStatus, String methodName) {
        if (testStatus != TEST_FAILED) {
            return;
        }
        String imagePath = String.format("%s%s%s_%s.png",
                FileUtils.createDirectoryIfNotExists("Screenshots"),
                File.separator,
                getDateTime(),
                methodName);
        try {
            File file = DriverFactory.getDriver().getScreenshotAs(OutputType.FILE);
            FileHandler.copy(file, new File(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save screenshot", e);
        }
    }

    public static void stopVideoRecording(WebDriver driver, int testStatus, String testName) {
        String media = ((CanRecordScreen) driver).stopRecordingScreen();

        if (testStatus == TEST_FAILED) {
            try (FileOutputStream stream = new FileOutputStream(new File(
                    FileUtils.createDirectoryIfNotExists("Videos"),
                    getDateTime() + "_" + testName + ".mp4"))) {
                stream.write(Base64.getDecoder().decode(media));
            } catch (IOException e) {
                LOGGER.error("Failed to save video for test: {}", testName, e);
                throw new RuntimeException("Failed to save video", e);
            }
        }
    }
}

