package com.zen.timer.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;

/**
 * 管理全局主题（明暗模式），通过为根节点设置不同的样式类来切换风格。
 */
public class ThemeManager {

    private static final String DARK_CLASS = "theme-dark";
    private static final String LIGHT_CLASS = "theme-light";

    private final BooleanProperty darkMode = new SimpleBooleanProperty(true);

    public ThemeManager() {
        // 默认启用暗色主题
    }

    public void bind(Parent root) {
        apply(root, darkMode.get());
        darkMode.addListener((obs, oldVal, newVal) -> apply(root, newVal));
    }

    private void apply(Parent root, boolean dark) {
        root.getStyleClass().removeAll(DARK_CLASS, LIGHT_CLASS);
        root.getStyleClass().add(dark ? DARK_CLASS : LIGHT_CLASS);
    }

    public BooleanProperty darkModeProperty() {
        return darkMode;
    }
}
