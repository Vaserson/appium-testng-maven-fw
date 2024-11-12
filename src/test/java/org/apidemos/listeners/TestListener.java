package org.apidemos.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apidemos.BaseTest;
import org.apidemos.driver.DriverFactory;
import org.apidemos.utils.TestUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

public class TestListener implements ITestListener {
    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);

    // Use and customize testng.ITestListener method
    public void onTestFailure(ITestResult result) {
        if (result.getThrowable() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.getThrowable().printStackTrace(pw);
            System.out.println(sw);
        }
        // Ensure the directory exists
        File screenshotDir = new File("Screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        String imagePath = "Screenshots" + File.separator + TestUtils.getDateTime() + "_" + result.getName() + ".png";
        String completeImagePath = System.getProperty("user.dir") + File.separator + imagePath;
        LOGGER.debug("@@@ completeImagePath: {}", completeImagePath);

        try {
            File file = DriverFactory.getDriver().getScreenshotAs(OutputType.FILE);
            FileHandler.copy(file, new File(imagePath));
            Reporter.log("Screenshot of the failed method");
            Reporter.log("<a href='"+ completeImagePath + "'> <img src='" + completeImagePath + "' height='1000' width='400'/> </a>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}