package org.apidemos.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apidemos.Constants.Platform;

import java.util.Objects;

public final class PlatformUtils {
    private static final Logger LOGGER = LogManager.getLogger(PlatformUtils.class);

    private static String platform = Platform.ANDROID.name();

    private PlatformUtils() {}


    public static String getPlatform () {
        LOGGER.info("Current Platform is [{}]", platform);
        return platform.toUpperCase();
    }

    public static void setPlatform (String platformName) {
        if (Objects.nonNull(platformName)) {
            platform = platformName.toUpperCase();
            LOGGER.info("Set Platform [{}]", platformName);
        } else if (Objects.nonNull(PropertyUtils.getProperty("platformName"))) {
            platform = PropertyUtils.getProperty("platformName");
            LOGGER.info("Set Platform from properties [{}]", platform);
        } else {
            LOGGER.info("Set Platform to default [{}]", platform);
        }
    }

}