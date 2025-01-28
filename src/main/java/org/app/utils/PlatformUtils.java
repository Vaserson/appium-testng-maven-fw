package org.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.enums.Platform;

import java.util.Objects;

public final class PlatformUtils {

    private static final Logger LOGGER = LogManager.getLogger(PlatformUtils.class);

    // Enum for platform types
    public enum PlatformType {
        ANDROID, IOS, UNKNOWN
    }

    private static PlatformType platform = PlatformType.ANDROID; // Default platform

    private PlatformUtils() {}

    /**
     * Retrieves the current platform.
     * @return the current PlatformType
     */
    public static PlatformType getPlatform() {
        LOGGER.info("Current Platform is [{}]", platform.name());
        return platform;
    }

    /**
     * Sets the platform based on user input or property values.
     * @param platformName platform name string (e.g., "android" or "ios")
     */
    public static void setPlatform(String platformName) {
        if (Objects.nonNull(platformName) && !platformName.trim().isEmpty()) {
            platform = parsePlatformType(platformName);
        } else {
            String propertyPlatform = PropertyUtils.getProperty("platformName");
            platform = propertyPlatform != null ? parsePlatformType(propertyPlatform) : PlatformType.ANDROID;
        }
        LOGGER.info("Platform set to [{}]", platform.name());
    }

    /**
     * Checks if the current platform is Android.
     * @return true if Android, false otherwise
     */
    public static boolean isAndroid() {
        return platform == PlatformType.ANDROID;
    }

    /**
     * Checks if the current platform is iOS.
     * @return true if iOS, false otherwise
     */
    public static boolean isIOS() {
        return platform == PlatformType.IOS;
    }

    /**
     * Converts a string to the corresponding PlatformType.
     * Defaults to UNKNOWN for invalid values.
     * @param platformName platform name as a string
     * @return PlatformType enum value
     */
    private static PlatformType parsePlatformType(String platformName) {
        try {
            return PlatformType.valueOf(platformName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid platform name [{}]. Defaulting to UNKNOWN.", platformName);
            return PlatformType.UNKNOWN;
        }
    }
}