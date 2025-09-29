package com.zen.timer.app;

import com.zen.timer.service.ThemeManager;
import com.zen.timer.view.CountdownView;
import com.zen.timer.viewmodel.CountdownViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 应用入口。负责初始化依赖容器、构建视图并在应用结束时清理资源。
 */
public class CountdownTimerApp extends Application {

    private transient AppContainer container;

    @Override
    public void start(Stage primaryStage) {
        container = new AppContainer();

        CountdownViewModel viewModel = container.provideViewModel();
        ThemeManager themeManager = container.getThemeManager();
        CountdownView view = new CountdownView(viewModel, themeManager);
        Scene scene = view.createScene();

        primaryStage.setTitle("Nebula 倒计时中心");
        primaryStage.setScene(scene);
        primaryStage.show();

        viewModel.initialize();
    }

    @Override
    public void stop() {
        if (container != null) {
            container.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
