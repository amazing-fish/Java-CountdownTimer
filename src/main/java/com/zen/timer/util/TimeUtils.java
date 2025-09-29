package com.zen.timer.util;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间相关的工具方法。
 */
public final class TimeUtils {

    private static final DateTimeFormatter FINISH_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private TimeUtils() {
    }

    public static long toSeconds(int hours, int minutes, int seconds) {
        return hours * 3600L + minutes * 60L + seconds;
    }

    public static String format(long seconds) {
        long absSeconds = Math.max(seconds, 0);
        long h = absSeconds / 3600;
        long m = (absSeconds % 3600) / 60;
        long s = absSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public static double progress(long remainingSeconds, long initialSeconds) {
        if (initialSeconds <= 0) {
            return 0;
        }
        double consumed = initialSeconds - Math.max(remainingSeconds, 0);
        return Math.min(1.0, Math.max(0.0, consumed / (double) initialSeconds));
    }

    public static int hoursPart(long seconds) {
        return (int) (seconds / 3600);
    }

    public static int minutesPart(long seconds) {
        return (int) ((seconds % 3600) / 60);
    }

    public static int secondsPart(long seconds) {
        return (int) (seconds % 60);
    }

    public static String estimateFinishText(long seconds) {
        if (seconds <= 0) {
            return "现在";
        }
        LocalTime finish = LocalTime.now().plusSeconds(seconds);
        return finish.format(FINISH_FORMATTER);
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        return format(seconds);
    }
}
