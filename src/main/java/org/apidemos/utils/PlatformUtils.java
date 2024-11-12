package org.apidemos.utils;

import java.util.Objects;

public final class PlatformUtils {
    private static String platform = "Android";

    private PlatformUtils() {}


    //TODO add realisations for TestNG, Properties etc.
    public static String getPlatform () {
        return platform.toUpperCase();
    }

    public static void setPlatform (String platformName) {
        if (Objects.nonNull(platformName)) {
            platform = platformName.toUpperCase();
        } else if (Objects.nonNull(PropertyUtils.getProperty("platformName"))) {
            platform = PropertyUtils.getProperty("platformName");
        }
    }

}