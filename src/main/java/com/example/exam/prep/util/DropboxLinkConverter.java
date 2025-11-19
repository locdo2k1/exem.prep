package com.example.exam.prep.util;

/**
 * Utility class for converting Dropbox shared links to direct download links.
 */
public class DropboxLinkConverter {

    /**
     * Converts a Dropbox shared link to a direct download link.
     * 
     * @param url The original Dropbox shared URL
     * @return The direct download URL
     */
    public static String toRawLink(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        // If it already contains ?dl= or ?rlkey= etc.
        if (url.contains("?")) {
            // Replace dl=0 or dl=1 with raw=1
            if (url.contains("dl=0") || url.contains("dl=1")) {
                return url.replaceAll("dl=0", "raw=1").replaceAll("dl=1", "raw=1");
            } 
            // If it doesn't have dl but has params, just append raw=1
            else if (!url.contains("raw=1")) {
                return url + "&raw=1";
            }
        } else {
            // If no query params at all
            return url + "?raw=1";
        }

        return url;
    }
}
