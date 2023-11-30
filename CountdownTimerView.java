import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
    这个类定义了倒计时计时器应用程序的用户界面。
    它使用JavaFX创建一个窗口，
    其中包含用于输入小时、分钟和秒的文本框，
    一个开始按钮，一个暂停/继续按钮，一个显示剩余时间的标签，以及一个进度条。
    所有的UI元素都被适当地布局和配置，以便用户与之交互。
    控制器将使用这个视图类的实例来更新UI以响应用户操作或倒计时的变化。
 **/

public class CountdownTimerView {
    // 定义一些常量，用于设置文本框的宽度和默认值，以及按钮的文本。
    private static final int TEXT_FIELD_WIDTH = 3;
    private static final String DEFAULT_VALUE = "00";
    private static final String START_BUTTON_TEXT = "开始";
    private static final String PAUSE_BUTTON_TEXT = "暂停";
    private static final String RESUME_BUTTON_TEXT = "继续";

    // UI组件的声明
    private TextField hourField, minuteField, secondField;
    private Label timeLabel;
    private Button startButton, toggleButton;
    private ProgressBar progressBar;

    // 构造函数，用于初始化视图并设置舞台。
    public CountdownTimerView(Stage primaryStage) {
        primaryStage.setTitle("倒计时器");
        GridPane grid = createLayout();

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 创建布局的私有方法，设置UI的布局和样式。
    private GridPane createLayout() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // 创建标签和文本框，并将它们添加到网格中。
        Label hourLabel = new Label("时");
        Label minuteLabel = new Label("分");
        Label secondLabel = new Label("秒");

        hourField = createTextField(DEFAULT_VALUE);
        minuteField = createTextField(DEFAULT_VALUE);
        secondField = createTextField(DEFAULT_VALUE);

        startButton = new Button(START_BUTTON_TEXT);
        toggleButton = new Button(PAUSE_BUTTON_TEXT);
        toggleButton.setDisable(true);  // 初始时禁用暂停/继续按钮

        timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 24; -fx-background-color: lightgray;");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        // 设置输入网格和按钮网格的布局
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(5);
        inputGrid.setVgap(5);
        inputGrid.add(hourLabel, 0, 0);
        inputGrid.add(hourField, 1, 0);
        inputGrid.add(minuteLabel, 0, 1);
        inputGrid.add(minuteField, 1, 1);
        inputGrid.add(secondLabel, 0, 2);
        inputGrid.add(secondField, 1, 2);

        HBox buttons = new HBox(10, startButton, toggleButton);
        buttons.setAlignment(Pos.CENTER);

        grid.add(inputGrid, 0, 0);
        grid.add(buttons, 0, 1);
        grid.add(timeLabel, 0, 2);
        grid.add(progressBar, 0, 3);

        return grid;
    }

    // 创建文本字段并为其设置默认值和行为
    private TextField createTextField(String defaultValue) {
        TextField field = new TextField(defaultValue);
        field.setPrefColumnCount(TEXT_FIELD_WIDTH);
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused && field.getText().equals(DEFAULT_VALUE)) {
                field.clear();
            } else if (!isNowFocused && field.getText().isEmpty()) {
                field.setText(DEFAULT_VALUE);
            }
        });
        return field;
    }

    // 下面是一系列getter方法，允许控制器访问和操作视图的各个部分。
    public TextField getHourField() { return hourField; }
    public TextField getMinuteField() { return minuteField; }
    public TextField getSecondField() { return secondField; }
    public Label getTimeLabel() { return timeLabel; }
    public Button getStartButton() { return startButton; }
    public Button getToggleButton() { return toggleButton; }
    public ProgressBar getProgressBar() { return progressBar; }
    public String getPauseButtonText() { return PAUSE_BUTTON_TEXT; }
    public String getResumeButtonText() { return RESUME_BUTTON_TEXT; }
}
