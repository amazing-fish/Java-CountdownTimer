import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.awt.Toolkit;

/**
    这个类是倒计时计时器应用程序的控制器部分，它管理视图和模型之间的交互。
    它设置并管理一个时间线，用于每秒更新一次倒计时，
    根据用户的操作来开始、暂停或继续倒计时，并更新视图以反映倒计时的当前状态。
    这个类是MVC架构中的关键组件，确保视图和模型之间的逻辑分离和协调一致。
 **/

public class CountdownTimerController {
    private CountdownTimerModel model; // 持有模型的引用
    private CountdownTimerView view; // 持有视图的引用
    private Timeline timeline; // 用于定时更新倒计时的时间线

    // 构造函数：初始化控制器，设置时间线和事件处理程序。
    public CountdownTimerController(CountdownTimerModel model, CountdownTimerView view) {
        this.model = model;
        this.view = view;
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateCountdown()));
        this.timeline.setCycleCount(Timeline.INDEFINITE);

        addEventHandlers(); // 添加事件处理程序
    }

    // 添加事件处理程序，响应视图的用户交互。
    private void addEventHandlers() {
        view.getStartButton().setOnAction(e -> startCountdown()); // 启动倒计时
        view.getToggleButton().setOnAction(e -> toggleCountdown()); // 暂停或继续倒计时

        // 监听模型中剩余秒数的变化，并更新视图。
        model.remainingSecondsProperty().addListener((obs, oldVal, newVal) -> {
            view.getTimeLabel().setText(model.convertToHMS(newVal.longValue())); // 更新显示的时间
            view.getProgressBar().setProgress(1 - (double) newVal.longValue() / convertInputToSeconds()); // 更新进度条
            if (newVal.longValue() <= 60) { // 如果时间小于等于60秒，字体变红
                view.getTimeLabel().setTextFill(Color.RED);
            } else {
                view.getTimeLabel().setTextFill(Color.BLACK);
            }
        });
    }

    // 开始倒计时的方法
    private void startCountdown() {
        long seconds = convertInputToSeconds(); // 从视图中获取输入并转换为秒
        if (seconds <= 0) {
            showAlert("请输入有效的时间！"); // 如果时间无效，显示警告
            return;
        }

        model.setRemainingSeconds(seconds); // 设置模型中的剩余秒数
        timeline.playFromStart(); // 开始或重启时间线
        view.getToggleButton().setDisable(false); // 启用暂停/继续按钮
        view.getToggleButton().setText(view.getPauseButtonText()); // 设置按钮文本为“暂停”
    }

    // 切换倒计时状态（暂停/继续）
    private void toggleCountdown() {
        if (timeline.getStatus() == Timeline.Status.PAUSED) {
            timeline.play(); // 继续倒计时
            view.getToggleButton().setText(view.getPauseButtonText()); // 设置按钮文本为“暂停”
        } else {
            timeline.pause(); // 暂停倒计时
            view.getToggleButton().setText(view.getResumeButtonText()); // 设置按钮文本为“继续”
        }
    }

    // 更新倒计时的方法
    private void updateCountdown() {
        model.updateCountdown(); // 调用模型的方法更新剩余时间
        if (model.isCountdownFinished()) { // 检查倒计时是否结束
            timeline.stop(); // 停止时间线
            showAlert("时间到！"); // 显示警告
            Toolkit.getDefaultToolkit().beep(); // 系统响铃
        }
    }

    // 从视图的输入字段中获取时间并转换为秒
    private long convertInputToSeconds() {
        try {
            int hours = Integer.parseInt(view.getHourField().getText());
            int minutes = Integer.parseInt(view.getMinuteField().getText());
            int seconds = Integer.parseInt(view.getSecondField().getText());
            return model.convertToSeconds(hours, minutes, seconds);
        } catch (NumberFormatException e) {
            return -1; // 如果输入无效，返回-1
        }
    }

    // 显示警告框的方法
    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
