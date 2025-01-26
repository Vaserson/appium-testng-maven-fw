package org.app.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    private static final Logger LOGGER = LogManager.getLogger(FileUtils.class);

    public static String createDirectoryIfNotExists(String dir) {
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                LOGGER.info("Directory [{}] was created", dir);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + dir, e);
            }
        } else {
            LOGGER.debug("Directory [{}] is already present", dir);
        }
        return dir;
    }

    public static String sanitizeFileName(String fileName) {
        String fileNameBefore = fileName;
        fileName = fileName.replaceAll("[^a-zA-Z0-9-_]", "_");
        fileName = fileName.replaceAll("_+", "_");
        fileName = fileName.replaceAll("^_+|_+$", "");
        LOGGER.info("Sanitizing file name: \nOriginal: '{}', \nSanitized: '{}'", fileNameBefore, fileName);
        return fileName;
    }
}