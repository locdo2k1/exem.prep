package com.example.exam.prep.util;

/**
 * Utility class for time formatting operations.
 */
public class TimeFormatHelper {

    private TimeFormatHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Converts seconds to a formatted time string in HH:mm:ss format.
     *
     * @param totalSeconds The total number of seconds to format
     * @return Formatted time string in HH:mm:ss format
     */
    public static String formatSecondsToHms(int totalSeconds) {
        if (totalSeconds < 0) {
            throw new IllegalArgumentException("Total seconds cannot be negative");
        }
        
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
