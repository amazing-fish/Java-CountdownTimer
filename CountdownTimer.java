import javafx.application.Application;
import javafx.stage.Stage;

public class CountdownTimer extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建模型
        CountdownTimerModel model = new CountdownTimerModel();

        // 创建视图
        CountdownTimerView view = new CountdownTimerView(primaryStage);

        // 创建控制器，并将模型和视图作为参数传递
        new CountdownTimerController(model, view);
    }

    public static void main(String[] args) {
        // 启动JavaFX应用程序
        launch(args);
    }
}
