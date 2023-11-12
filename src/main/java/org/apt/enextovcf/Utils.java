package org.apt.enextovcf;

import java.io.File;

public class Utils {

    @SuppressWarnings("unused")
    public static String makeUniqueFileName(File directory, String fileName) {
        // Get the file name without the extension
        String baseName = fileName;
        String extension = "";

        int dotIndex = baseName.lastIndexOf(".");
        if (dotIndex >= 0) {
            extension = baseName.substring(dotIndex);
            baseName = baseName.substring(0, dotIndex);
        }

        // Remove invalid characters and replace them with underscores
        baseName = baseName.replaceAll("[^a-zA-Z0-9.-]", "_");

        // Check if the file name is unique in the directory
        String uniqueFileName = baseName + extension;
        int count = 1;

        while (new File(directory, uniqueFileName).exists()) {
            uniqueFileName = baseName + "_" + count + extension;
            count++;
        }
        return uniqueFileName;
    }

}
