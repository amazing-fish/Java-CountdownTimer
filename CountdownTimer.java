import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import java.awt.Toolkit;

public class CountdownTimer extends Application {
    private static final String INITIAL_TIME_FORMAT = "00";
    private static final int TEXT_FIELD_WIDTH = 3;
    private static final String COUNTDOWN_FINISHED_TEXT = "时间到！";
    private static final String INVALID_TIME_TEXT = "请输入有效的时间！";
    private static final String START_BUTTON_TEXT = "开始";
    private static final String PAUSE_BUTTON_TEXT = "暂停";
    private static final String RESUME_BUTTON_TEXT = "继续";

    private TextField hourField, minuteField, secondField;
    private Label timeLabel;
    private Timeline timeline;
    private LongProperty remainingSeconds = new SimpleLongProperty();
    private Button toggleButton;
    private ProgressBar progressBar;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("倒计时器");
        GridPane grid = createLayout();
        initializeCountdownMechanism();

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createLayout() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        hourField = createTextField(INITIAL_TIME_FORMAT);
        minuteField = createTextField(INITIAL_TIME_FORMAT);
        secondField = createTextField(INITIAL_TIME_FORMAT);

        Button startButton = new Button(START_BUTTON_TEXT);
        startButton.setOnAction(e -> startCountdown());

        toggleButton = new Button(PAUSE_BUTTON_TEXT);
        toggleButton.setOnAction(e -> toggleCountdown());
        toggleButton.setDisable(true);

        timeLabel = new Label(convertToHMS(0));
        timeLabel.setStyle("-fx-font-size: 24; -fx-background-color: lightgray;");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        HBox timeFields = new HBox(5, hourField, minuteField, secondField);
        timeFields.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(10, startButton, toggleButton);
        buttons.setAlignment(Pos.CENTER);

        grid.add(timeFields, 0, 0);
        grid.add(buttons, 0, 1);
        grid.add(timeLabel, 0, 2);
        grid.add(progressBar, 0, 3);

        return grid;
    }

    private void initializeCountdownMechanism() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateCountdown()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        remainingSeconds.addListener((obs, oldVal, newVal) -> {
            timeLabel.setText(convertToHMS(newVal.longValue()));
            progressBar.setProgress(1 - (double) newVal.longValue() / convertToSeconds());
            if (newVal.longValue() <= 60) {
                timeLabel.setTextFill(Color.RED);
            } else {
                timeLabel.setTextFill(Color.BLACK);
            }
        });
    }

    private TextField createTextField(String defaultValue) {
        TextField field = new TextField(defaultValue);
        field.setPrefColumnCount(TEXT_FIELD_WIDTH);
        return field;
    }

    private void toggleCountdown() {
        if (timeline.getStatus() == Timeline.Status.PAUSED) {
            timeline.play();
            toggleButton.setText(PAUSE_BUTTON_TEXT);
        } else {
            timeline.pause();
            toggleButton.setText(RESUME_BUTTON_TEXT);
        }
    }

    private void startCountdown() {
        long seconds = convertToSeconds();
        if (seconds <= 0) {
            showAlert(INVALID_TIME_TEXT);
            return;
        }

        remainingSeconds.set(seconds);
        timeline.playFromStart();
        toggleButton.setDisable(false);
        toggleButton.setText(PAUSE_BUTTON_TEXT);
    }

    private void updateCountdown() {
        long currentValue = remainingSeconds.get();
        if (currentValue > 0) {
            remainingSeconds.set(currentValue - 1);
        } else {
            timeline.stop();
            showAlert(COUNTDOWN_FINISHED_TEXT);
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private long convertToSeconds() {
        try {
            int hours = Integer.parseInt(hourField.getText());
            int minutes = Integer.parseInt(minuteField.getText());
            int seconds = Integer.parseInt(secondField.getText());
            return hours * 3600L + minutes * 60L + seconds;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String convertToHMS(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
