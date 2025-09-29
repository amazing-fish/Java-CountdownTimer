package com.zen.timer.view;

import com.zen.timer.model.CountdownStatus;
import com.zen.timer.model.PresetDuration;
import com.zen.timer.service.ThemeManager;
import com.zen.timer.viewmodel.CountdownViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.List;

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

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("app-scroll");
        root.setCenter(scrollPane);

        VBox content = new VBox(32);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(48, 48, 64, 48));
        content.setFillWidth(true);
        scrollPane.setContent(content);
        scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) ->
                content.setPrefWidth(newBounds.getWidth()));

        Label title = new Label("Nebula 倒计时中心");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("专注、节奏与美感融为一体的时间体验");
        subtitle.getStyleClass().add("app-subtitle");

        VBox header = new VBox(8, title, subtitle);
        header.setAlignment(Pos.CENTER);

        ToggleButton themeToggle = new ToggleButton();
        themeToggle.getStyleClass().add("theme-toggle");
        themeToggle.selectedProperty().bindBidirectional(themeManager.darkModeProperty());
        themeToggle.textProperty().bind(Bindings.when(themeManager.darkModeProperty())
                .then("夜间模式")
                .otherwise("日间模式"));
        themeToggle.setTooltip(new Tooltip("切换整体视觉主题"));

        BorderPane hero = new BorderPane();
        hero.setCenter(header);
        hero.setRight(themeToggle);
        hero.setMaxWidth(1200);
        BorderPane.setAlignment(themeToggle, Pos.TOP_RIGHT);
        content.getChildren().add(hero);
        VBox.setMargin(hero, new Insets(0, 0, 16, 0));

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
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.progressProperty().bind(viewModel.progressProperty());

        Label finishLabel = new Label();
        finishLabel.getStyleClass().add("finish-time-label");
        finishLabel.textProperty().bind(Bindings.concat("预计完成时间：", viewModel.finishTimeTextProperty()));

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        Circle halo = new Circle(140);
        halo.getStyleClass().add("halo-circle");

        StackPane timeStack = new StackPane(halo, timeDisplay);
        timeStack.setAlignment(Pos.CENTER);

        VBox displayCard = new VBox(16, timeStack, progressBar, finishLabel, statusLabel);
        displayCard.getStyleClass().addAll("display-card", "content-card");
        displayCard.setAlignment(Pos.CENTER);
        displayCard.setFillWidth(true);
        displayCard.setPadding(new Insets(24));
        displayCard.setMinWidth(280);
        displayCard.setPrefWidth(360);
        displayCard.setMaxWidth(Double.MAX_VALUE);

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
        timeInputs.setAlignment(Pos.CENTER_LEFT);

        FlowPane presetPane = new FlowPane();
        presetPane.setHgap(12);
        presetPane.setVgap(12);
        presetPane.setAlignment(Pos.TOP_LEFT);
        presetPane.getStyleClass().add("preset-pane");

        bindPresets(presetPane, hourSpinner, minuteSpinner, secondSpinner);

        HBox controlBar = buildControlBar(hourSpinner, minuteSpinner, secondSpinner);
        controlBar.setMaxWidth(Double.MAX_VALUE);

        Label inputTitle = new Label("时间输入与控制");
        inputTitle.getStyleClass().add("card-title");

        Label inputSubtitle = new Label("自定义倒计时，或快速套用精心挑选的番茄节奏");
        inputSubtitle.getStyleClass().add("card-subtitle");

        Label presetLabel = new Label("推荐预设");
        presetLabel.getStyleClass().add("section-title");

        VBox presetSection = new VBox(8, presetLabel, presetPane);

        VBox inputCard = new VBox(20);
        inputCard.getStyleClass().add("content-card");
        inputCard.setAlignment(Pos.TOP_LEFT);
        inputCard.setPadding(new Insets(24));
        inputCard.setMinWidth(280);
        inputCard.setPrefWidth(360);
        inputCard.setMaxWidth(Double.MAX_VALUE);
        inputCard.getChildren().addAll(inputTitle, inputSubtitle, timeInputs, presetSection, controlBar);

        presetPane.prefWrapLengthProperty().bind(Bindings.max(0, inputCard.widthProperty().subtract(48)));

        VBox roadmapCard = buildRoadmapCard();

        GridPane cardGrid = new GridPane();
        cardGrid.setHgap(24);
        cardGrid.setVgap(24);
        cardGrid.setAlignment(Pos.TOP_CENTER);
        cardGrid.setMaxWidth(1200);

        List<Node> cards = List.of(displayCard, inputCard, roadmapCard);
        cards.forEach(card -> {
            GridPane.setHgrow(card, Priority.ALWAYS);
            GridPane.setVgrow(card, Priority.NEVER);
        });

        configureResponsiveLayout(content, cardGrid, cards);

        content.getChildren().add(cardGrid);

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
        controls.setAlignment(Pos.CENTER_LEFT);
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

    private void configureResponsiveLayout(VBox container, GridPane gridPane, List<Node> cards) {
        container.widthProperty().addListener((obs, oldWidth, newWidth) ->
                updateResponsiveGrid(gridPane, cards, newWidth.doubleValue()));
        double initialWidth = container.getWidth() > 0 ? container.getWidth() : 900;
        updateResponsiveGrid(gridPane, cards, initialWidth);
    }

    private void updateResponsiveGrid(GridPane gridPane, List<Node> cards, double width) {
        if (width <= 0) {
            return;
        }
        int columns = width >= 1280 ? 3 : width >= 900 ? 2 : 1;
        gridPane.getChildren().setAll(cards);
        for (int i = 0; i < cards.size(); i++) {
            Node card = cards.get(i);
            GridPane.setColumnIndex(card, i % columns);
            GridPane.setRowIndex(card, i / columns);
        }
    }

    private VBox buildRoadmapCard() {
        Label title = new Label("Nebula Roadmap");
        title.getStyleClass().add("card-title");

        Label subtitle = new Label("循序渐进迭代，让倒计时体验持续进化");
        subtitle.getStyleClass().add("card-subtitle");

        VBox roadmapList = new VBox(12,
                roadmapItem("v1.1 焕新界面", "响应式布局、卡片化信息展示", "进行中", "status-active"),
                roadmapItem("v1.2 深度专注", "多场景 Preset、跨设备同步", "策划中", "status-planned"),
                roadmapItem("v1.3 工作流加速", "自动提醒、统计洞察与分享", "展望", "status-future"));

        VBox roadmapCard = new VBox(18, title, subtitle, roadmapList);
        roadmapCard.getStyleClass().add("content-card");
        roadmapCard.setAlignment(Pos.TOP_LEFT);
        roadmapCard.setPadding(new Insets(24));
        roadmapCard.setMinWidth(280);
        roadmapCard.setPrefWidth(320);
        roadmapCard.setMaxWidth(Double.MAX_VALUE);
        return roadmapCard;
    }

    private VBox roadmapItem(String milestone, String focus, String status, String statusStyle) {
        Label milestoneLabel = new Label(milestone);
        milestoneLabel.getStyleClass().add("roadmap-phase");

        Label focusLabel = new Label(focus);
        focusLabel.getStyleClass().add("roadmap-focus");

        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().addAll("roadmap-status", statusStyle);

        VBox item = new VBox(4, milestoneLabel, focusLabel, statusLabel);
        item.getStyleClass().add("roadmap-item");
        return item;
    }
}
