package com.zen.timer.model;

import com.zen.timer.util.TimeUtils;

/**
 * 预设时长的不可变数据结构。
 */
public record PresetDuration(String label, long seconds) {

    public int hours() {
        return TimeUtils.hoursPart(seconds);
    }

    public int minutes() {
        return TimeUtils.minutesPart(seconds);
    }

    public int secondsPart() {
        return TimeUtils.secondsPart(seconds);
    }
}
