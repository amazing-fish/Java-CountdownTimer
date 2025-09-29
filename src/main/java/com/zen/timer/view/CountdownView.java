package com.zen.timer.view;

import com.zen.timer.model.CountdownStatus;
import com.zen.timer.model.PresetDuration;
import com.zen.timer.service.ThemeManager;
import com.zen.timer.viewmodel.CountdownViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * 现代化的倒计时界面，包含主题切换、预设选择、进度条等新特性。
 */
public class CountdownView {

    private final CountdownViewModel viewModel;
    private final ThemeManager themeManager;

    public CountdownView(CountdownViewModel viewModel, ThemeManager themeManager) {
        this.viewModel = viewModel;
        this.themeManager = themeManager;
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");

        VBox center = new VBox(24);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(32));

        Label title = new Label("Nebula 倒计时中心");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("专注、节奏与美感融为一体的时间体验");
        subtitle.getStyleClass().add("app-subtitle");

        VBox header = new VBox(8, title, subtitle);
        header.setAlignment(Pos.CENTER);

        Label timeDisplay = new Label();
        timeDisplay.getStyleClass().add("time-display");
        timeDisplay.textProperty().bind(viewModel.formattedTimeProperty());
        viewModel.criticalProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (!timeDisplay.getStyleClass().contains("critical")) {
                    timeDisplay.getStyleClass().add("critical");
                }
            } else {
                timeDisplay.getStyleClass().remove("critical");
            }
        });

        ProgressBar progressBar = new ProgressBar();
        progressBar.getStyleClass().add("countdown-progress");
        progressBar.setPrefWidth(420);
        progressBar.progressProperty().bind(viewModel.progressProperty());

        Label finishLabel = new Label();
        finishLabel.getStyleClass().add("finish-time-label");
        finishLabel.textProperty().bind(Bindings.concat("预计完成时间：", viewModel.finishTimeTextProperty()));

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        VBox displayCard = new VBox(16, timeDisplay, progressBar, finishLabel, statusLabel);
        displayCard.getStyleClass().add("display-card");
        displayCard.setAlignment(Pos.CENTER);
        displayCard.setPadding(new Insets(24));

        Spinner<Integer> hourSpinner = new Spinner<>();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        Spinner<Integer> minuteSpinner = new Spinner<>();
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        Spinner<Integer> secondSpinner = new Spinner<>();
        secondSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        hourSpinner.disableProperty().bind(viewModel.allowEditsProperty().not());
        minuteSpinner.disableProperty().bind(viewModel.allowEditsProperty().not());
        secondSpinner.disableProperty().bind(viewModel.allowEditsProperty().not());

        HBox timeInputs = new HBox(16,
                labeledBox("小时", hourSpinner),
                labeledBox("分钟", minuteSpinner),
                labeledBox("秒钟", secondSpinner));
        timeInputs.getStyleClass().add("time-inputs");
        timeInputs.setAlignment(Pos.CENTER);

        FlowPane presetPane = new FlowPane();
        presetPane.setHgap(12);
        presetPane.setVgap(12);
        presetPane.setAlignment(Pos.CENTER);
        presetPane.getStyleClass().add("preset-pane");

        bindPresets(presetPane, hourSpinner, minuteSpinner, secondSpinner);

        HBox controlBar = buildControlBar(hourSpinner, minuteSpinner, secondSpinner);

        ToggleButton themeToggle = new ToggleButton();
        themeToggle.getStyleClass().add("theme-toggle");
        themeToggle.selectedProperty().bindBidirectional(themeManager.darkModeProperty());
        themeToggle.textProperty().bind(Bindings.when(themeManager.darkModeProperty())
                .then("夜间模式")
                .otherwise("日间模式"));
        themeToggle.setTooltip(new Tooltip("切换整体视觉主题"));

        Circle halo = new Circle(140);
        halo.getStyleClass().add("halo-circle");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        center.getChildren().addAll(header, halo, displayCard, timeInputs, presetPane, controlBar, themeToggle, spacer);
        root.setCenter(center);

        Scene scene = new Scene(root, 900, 640);
        scene.getStylesheets().add(getClass().getResource("/com/zen/timer/styles/app-theme.css").toExternalForm());
        themeManager.bind(root);
        return scene;
    }

    private HBox buildControlBar(Spinner<Integer> hourSpinner, Spinner<Integer> minuteSpinner, Spinner<Integer> secondSpinner) {
        Button startButton = new Button("开始");
        Button toggleButton = new Button();
        Button resetButton = new Button("复位");

        startButton.getStyleClass().add("primary-action");
        toggleButton.getStyleClass().add("secondary-action");
        resetButton.getStyleClass().add("ghost-action");

        startButton.setOnAction(event -> {
            boolean started = viewModel.start(
                    hourSpinner.getValue(),
                    minuteSpinner.getValue(),
                    secondSpinner.getValue()
            );
            if (started) {
                viewModel.selectPreset(null);
            }
        });

        toggleButton.setOnAction(event -> {
            CountdownStatus status = viewModel.statusProperty().get();
            if (status == CountdownStatus.RUNNING) {
                viewModel.pause();
            } else if (status == CountdownStatus.PAUSED) {
                viewModel.resume();
            }
        });

        resetButton.setOnAction(event -> viewModel.reset());

        toggleButton.textProperty().bind(Bindings.createStringBinding(() -> {
            CountdownStatus status = viewModel.statusProperty().get();
            return status == CountdownStatus.RUNNING ? "暂停" : "继续";
        }, viewModel.statusProperty()));

        toggleButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            CountdownStatus status = viewModel.statusProperty().get();
            return status == CountdownStatus.IDLE || status == CountdownStatus.COMPLETED;
        }, viewModel.statusProperty()));

        startButton.disableProperty().bind(viewModel.runningProperty());
        resetButton.disableProperty().bind(Bindings.createBooleanBinding(() -> viewModel.statusProperty().get() == CountdownStatus.IDLE,
                viewModel.statusProperty()));

        HBox controls = new HBox(18, startButton, toggleButton, resetButton);
        controls.setAlignment(Pos.CENTER);
        controls.getStyleClass().add("control-bar");
        return controls;
    }

    private void bindPresets(FlowPane presetPane,
                             Spinner<Integer> hourSpinner,
                             Spinner<Integer> minuteSpinner,
                             Spinner<Integer> secondSpinner) {
        viewModel.getPresets().addListener((ListChangeListener<PresetDuration>) change ->
                rebuildPresetButtons(presetPane, hourSpinner, minuteSpinner, secondSpinner));
        rebuildPresetButtons(presetPane, hourSpinner, minuteSpinner, secondSpinner);
    }

    private void rebuildPresetButtons(FlowPane presetPane,
                                      Spinner<Integer> hourSpinner,
                                      Spinner<Integer> minuteSpinner,
                                      Spinner<Integer> secondSpinner) {
        presetPane.getChildren().clear();
        viewModel.getPresets().forEach(preset -> {
            ToggleButton button = new ToggleButton(preset.label());
            button.getStyleClass().add("preset-button");
            button.setOnAction(event -> {
                if (button.isSelected()) {
                    viewModel.selectPreset(preset);
                    hourSpinner.getValueFactory().setValue(preset.hours());
                    minuteSpinner.getValueFactory().setValue(preset.minutes());
                    secondSpinner.getValueFactory().setValue(preset.secondsPart());
                } else {
                    viewModel.selectPreset(null);
                }
            });
            button.disableProperty().bind(viewModel.allowEditsProperty().not());
            viewModel.activePresetProperty().addListener((obs, oldVal, newVal) ->
                    button.setSelected(newVal != null && newVal.equals(preset)));
            presetPane.getChildren().add(button);
        });
    }

    private VBox labeledBox(String labelText, Spinner<Integer> spinner) {
        Label label = new Label(labelText);
        label.getStyleClass().add("input-label");
        spinner.setPrefWidth(100);
        VBox box = new VBox(6, label, spinner);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}
