package com.zen.timer.app;

import com.zen.timer.service.SoundService;
import com.zen.timer.service.ThemeManager;
import com.zen.timer.service.TimerService;
import com.zen.timer.viewmodel.CountdownViewModel;

/**
 * 简单的依赖注入容器，集中管理应用中需要共享的服务实例。
 */
public class AppContainer {

    private final TimerService timerService;
    private final SoundService soundService;
    private final ThemeManager themeManager;

    public AppContainer() {
        this.timerService = new TimerService();
        this.soundService = new SoundService();
        this.themeManager = new ThemeManager();
    }

    public CountdownViewModel provideViewModel() {
        return new CountdownViewModel(timerService, soundService);
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public void shutdown() {
        timerService.shutdown();
    }
}
