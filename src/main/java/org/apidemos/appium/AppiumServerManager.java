package org.apidemos.appium;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import java.io.File;
import java.util.Objects;

public class AppiumServerManager {
    private static AppiumDriverLocalService appiumService;

    private AppiumServerManager() {}

    public static AppiumDriverLocalService startAppiumService(String ipAddress, int port) {
        appiumService = new AppiumServiceBuilder()
                .withAppiumJS(new File("C:\\Users\\PC\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js"))
                .withArgument(() -> "--use-plugins", "images")
                .withIPAddress(ipAddress)
                .usingPort(port)
                .build();
        appiumService.start();
        return appiumService;
    }

    public static void stopAppiumService() {
        if (Objects.nonNull(appiumService)) {
            appiumService.stop();
        }
    }
}
