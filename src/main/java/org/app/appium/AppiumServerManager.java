package org.app.appium;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.exceptions.FrameworkException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class AppiumServerManager {
    private static final Logger LOGGER = LogManager.getLogger(AppiumServerManager.class);

    private static AppiumDriverLocalService appiumService;

    private AppiumServerManager() {}

    public static AppiumDriverLocalService startAppiumService(String host, int port) {
        LOGGER.info("Appium service starting at [{}:{}]", host, port);
        try {
            String appiumJSPath = resolveAppiumJSPath();
            LOGGER.info("Resolved Appium main.js path: [{}]", appiumJSPath);

            appiumService = new AppiumServiceBuilder()
                    .withAppiumJS(new File(appiumJSPath))
                    .withArgument(() -> "--use-plugins", "images")
                    .withIPAddress(host)
                    .usingPort(port)
                    .build();
            appiumService.start();
        } catch (Exception e) {
            LOGGER.error("Appium service failed to start at {}:{}", host, port);
            throw new FrameworkException("Failed to start Appium service at " + host + ":" + port, e);
        }
        LOGGER.info("Appium service started at {}:{}", host, port);
        return appiumService;
    }

    public static void stopAppiumService() {
        LOGGER.info("Appium service stopping");
        if (Objects.nonNull(appiumService)) {
            appiumService.stop();
        }
    }

    private static String resolveAppiumJSPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String command = osName.contains("win") ? "where" : "which";
        String appiumExecutable = "appium";

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command, appiumExecutable);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String appiumPath = reader.readLine();
                if (appiumPath != null && !appiumPath.isEmpty()) {
                    if (osName.contains("win")) {
                        return appiumPath.replace("appium", "node_modules\\appium\\build\\lib\\main.js");
                    } else {
                        return appiumPath.replace("appium", "node_modules/appium/build/lib/main.js");
                    }
                } else {
                    LOGGER.error("Appium executable not found in PATH.");
                    throw new FrameworkException("Appium executable not found in PATH.");
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to resolve Appium main.js path", e);
            throw new FrameworkException("Appium main.js path resolution failed", e);
        }
    }

    public static String getAppiumServiceUrl() {
        if (appiumService != null) {
            try {
                URL serviceUrl = appiumService.getUrl();
                return serviceUrl != null ? serviceUrl.toString() : null;
            } catch (Exception e) {
                LOGGER.error("Error fetching Appium service URL", e);
            }
        }
        return null;
    }
}