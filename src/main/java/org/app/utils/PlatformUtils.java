package org.app.utils;

import org.app.enums.Platform;
import org.app.exceptions.UnsupportedPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class PlatformUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformUtils.class);


    private static Platform platform = Platform.ANDROID; // Default platform

    private PlatformUtils() {}

    /**
     * Retrieves the current platform.
     * @return the current PlatformType
     */
    public static Platform getPlatform() {
        LOGGER.info("Current Platform is [{}]", platform.name());
        return platform;
    }

    /**
     * Sets the platform based on user input or property values.
     * @param platformName platform name string (e.g., "android", "ios" or "web")
     */
    public static void setPlatform(Platform platformName) {
        if (Objects.nonNull(platformName)) {
            platform = platformName;
        } else {
            String propertyPlatform = PropertyUtils.getProperty("platform");
            platform = propertyPlatform != null ? parsePlatformType(propertyPlatform) : Platform.ANDROID;
        }
        LOGGER.info("Platform set to [{}]", platform.name());
    }

    /**
     * Checks if the current platform is Android.
     * @return true if Android, false otherwise
     */
    public static boolean isAndroid() {
        return platform == Platform.ANDROID;
    }

    /**
     * Checks if the current platform is iOS.
     * @return true if iOS, false otherwise
     */
    public static boolean isIOS() {
        return platform == Platform.IOS;
    }

    /**
     * Checks if the current platform is WEB.
     * @return true if WEB, false otherwise
     */
    public static boolean isWeb() {
        return platform == Platform.WEB;
    }

    /**
     * Converts a string to the corresponding PlatformType.
     * Defaults to UNKNOWN for invalid values.
     * @param platformName platform name as a string
     * @return PlatformType enum value
     */
    private static Platform parsePlatformType(String platformName) {
        try {
            return Platform.valueOf(platformName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid platform name [{}]. Defaulting to UNKNOWN.", platformName);
            throw new UnsupportedPlatformException("Invalid platform name [" + platformName + "].", e);
        }
    }
}