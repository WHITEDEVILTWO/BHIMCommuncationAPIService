package org.npci.bhim.BHIMSMSserviceAPI.utils;

import java.util.HashMap;
import java.util.Map;

public class MediaUtils {

    // Map extensions to MIME types
    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("bmp", "image/bmp");
        MIME_TYPES.put("webp", "image/webp");
        MIME_TYPES.put("mp4", "video/mp4");
        MIME_TYPES.put("mp3", "audio/mpeg");
        MIME_TYPES.put("wav", "audio/wav");
        MIME_TYPES.put("pdf", "application/pdf");
        // Add more as needed
    }

    /**
     * Extracts file extension and maps to MIME type.
     *
     * @param url The media URL
     * @return MIME type like "image/png" or "application/pdf"
     */
    public static String getMediaFormatFromUrl(String url) {
        if (url == null || url.isEmpty()) return "";

        // Remove query params
        String cleanUrl = url.split("\\?")[0];

        // Extract file name
        String fileName = cleanUrl.substring(cleanUrl.lastIndexOf("/") + 1);

        // Extract extension
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1).toLowerCase();
            return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
        }

        return "application/octet-stream"; // default binary
    }
}
