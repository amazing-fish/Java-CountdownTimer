package com.zen.timer.service;

import java.awt.Toolkit;

/**
 * 负责播放提醒声音。当前实现使用系统蜂鸣声，未来可以在此扩展为多音效或自定义音频。 
 */
public class SoundService {

    public void playCompletionTone() {
        Toolkit.getDefaultToolkit().beep();
    }
}
