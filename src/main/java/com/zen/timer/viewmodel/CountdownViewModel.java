package com.zen.timer.viewmodel;

import com.zen.timer.model.CountdownStatus;
import com.zen.timer.model.PresetDuration;
import com.zen.timer.service.SoundService;
import com.zen.timer.service.TimerService;
import com.zen.timer.util.TimeUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Duration;
import java.util.List;

/**
 * 倒计时的视图模型，桥接 UI 与底层服务。
 */
public class CountdownViewModel {

    private final TimerService timerService;
    private final SoundService soundService;

    private final LongProperty initialSeconds = new SimpleLongProperty(0);
    private final LongProperty remainingSeconds = new SimpleLongProperty(0);

    private final StringProperty formattedTime = new SimpleStringProperty(TimeUtils.format(0));
    private final StringProperty statusMessage = new SimpleStringProperty("等待开始");
    private final StringProperty finishTimeText = new SimpleStringProperty("-");

    private final ObjectProperty<CountdownStatus> status = new SimpleObjectProperty<>(CountdownStatus.IDLE);
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final BooleanProperty critical = new SimpleBooleanProperty(false);
    private final BooleanProperty allowEdits = new SimpleBooleanProperty(true);

    private final ObservableList<PresetDuration> presets = FXCollections.observableArrayList();
    private final ReadOnlyObjectWrapper<PresetDuration> activePreset = new ReadOnlyObjectWrapper<>();

    private final ReadOnlyObjectWrapper<Double> progress = new ReadOnlyObjectWrapper<>(0.0);

    public CountdownViewModel(TimerService timerService, SoundService soundService) {
        this.timerService = timerService;
        this.soundService = soundService;
        progress.bind(Bindings.createDoubleBinding(
                () -> TimeUtils.progress(remainingSeconds.get(), initialSeconds.get()),
                remainingSeconds, initialSeconds));
        allowEdits.bind(running.not());
    }

    public void initialize() {
        presets.setAll(List.of(
                new PresetDuration("番茄钟 25 分钟", Duration.ofMinutes(25).getSeconds()),
                new PresetDuration("短休息 5 分钟", Duration.ofMinutes(5).getSeconds()),
                new PresetDuration("长休息 15 分钟", Duration.ofMinutes(15).getSeconds()),
                new PresetDuration("晨练 45 分钟", Duration.ofMinutes(45).getSeconds()),
                new PresetDuration("深度工作 90 分钟", Duration.ofMinutes(90).getSeconds())
        ));
    }

    public boolean start(int hours, int minutes, int seconds) {
        long totalSeconds = TimeUtils.toSeconds(hours, minutes, seconds);
        if (totalSeconds <= 0) {
            statusMessage.set("请输入一个大于 0 的时间");
            return false;
        }
        initialSeconds.set(totalSeconds);
        remainingSeconds.set(totalSeconds);
        formattedTime.set(TimeUtils.format(totalSeconds));
        finishTimeText.set(TimeUtils.estimateFinishText(totalSeconds));
        running.set(true);
        status.set(CountdownStatus.RUNNING);
        statusMessage.set("倒计时进行中");
        critical.set(totalSeconds <= 60);
        timerService.start(totalSeconds, this::handleTick, this::handleCompletion);
        return true;
    }

    public void pause() {
        if (status.get() != CountdownStatus.RUNNING) {
            return;
        }
        timerService.pause();
        running.set(false);
        status.set(CountdownStatus.PAUSED);
        statusMessage.set("倒计时已暂停");
    }

    public void resume() {
        if (status.get() != CountdownStatus.PAUSED) {
            return;
        }
        running.set(true);
        status.set(CountdownStatus.RUNNING);
        statusMessage.set("倒计时进行中");
        timerService.resume(remainingSeconds.get(), this::handleTick, this::handleCompletion);
    }

    public void reset() {
        timerService.stop();
        running.set(false);
        status.set(CountdownStatus.IDLE);
        formattedTime.set(TimeUtils.format(initialSeconds.get()));
        remainingSeconds.set(initialSeconds.get());
        finishTimeText.set("-");
        statusMessage.set("已复位，等待开始");
        critical.set(false);
    }

    public void selectPreset(PresetDuration preset) {
        if (preset == null) {
            activePreset.set(null);
            return;
        }
        activePreset.set(preset);
        initialSeconds.set(preset.seconds());
        remainingSeconds.set(preset.seconds());
        formattedTime.set(TimeUtils.format(preset.seconds()));
        finishTimeText.set("-");
        statusMessage.set("已选择预设：" + preset.label());
        critical.set(preset.seconds() <= 60);
    }

    private void handleTick(long secondsLeft) {
        remainingSeconds.set(secondsLeft);
        formattedTime.set(TimeUtils.format(secondsLeft));
        finishTimeText.set(TimeUtils.estimateFinishText(secondsLeft));
        critical.set(secondsLeft <= 60);
    }

    private void handleCompletion() {
        running.set(false);
        status.set(CountdownStatus.COMPLETED);
        formattedTime.set(TimeUtils.format(0));
        remainingSeconds.set(0);
        finishTimeText.set("现在");
        statusMessage.set("时间到！");
        critical.set(false);
        soundService.playCompletionTone();
    }

    public StringProperty formattedTimeProperty() {
        return formattedTime;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public ReadOnlyObjectProperty<Double> progressProperty() {
        return progress.getReadOnlyProperty();
    }

    public BooleanProperty runningProperty() {
        return running;
    }

    public ObjectProperty<CountdownStatus> statusProperty() {
        return status;
    }

    public BooleanProperty allowEditsProperty() {
        return allowEdits;
    }

    public BooleanProperty criticalProperty() {
        return critical;
    }

    public StringProperty finishTimeTextProperty() {
        return finishTimeText;
    }

    public ObservableList<PresetDuration> getPresets() {
        return presets;
    }

    public ReadOnlyObjectProperty<PresetDuration> activePresetProperty() {
        return activePreset.getReadOnlyProperty();
    }

    public long getRemainingSeconds() {
        return remainingSeconds.get();
    }
}
