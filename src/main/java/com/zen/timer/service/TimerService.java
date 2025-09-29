package com.zen.timer.service;

import javafx.application.Platform;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

/**
 * 使用单线程调度器驱动的精确倒计时服务，提供启动、暂停与恢复的能力。
 */
public class TimerService {

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> activeTask;

    public TimerService() {
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable, "countdown-timer");
            thread.setDaemon(true);
            return thread;
        };
        this.executor = Executors.newSingleThreadScheduledExecutor(factory);
    }

    public synchronized void start(long totalSeconds, LongConsumer onTick, Runnable onFinished) {
        Objects.requireNonNull(onTick, "onTick 必须提供");
        Objects.requireNonNull(onFinished, "onFinished 必须提供");
        stopInternal();
        if (totalSeconds <= 0) {
            Platform.runLater(onFinished);
            return;
        }
        AtomicLong remaining = new AtomicLong(totalSeconds);
        activeTask = executor.scheduleAtFixedRate(() -> {
            long next = remaining.decrementAndGet();
            long safeNext = Math.max(next, 0);
            Platform.runLater(() -> onTick.accept(safeNext));
            if (next <= 0) {
                stopInternal();
                Platform.runLater(onFinished);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public synchronized void resume(long remainingSeconds, LongConsumer onTick, Runnable onFinished) {
        start(remainingSeconds, onTick, onFinished);
    }

    public synchronized void pause() {
        stopInternal();
    }

    public synchronized void stop() {
        stopInternal();
    }

    private void stopInternal() {
        if (activeTask != null) {
            activeTask.cancel(false);
            activeTask = null;
        }
    }

    public void shutdown() {
        stopInternal();
        executor.shutdownNow();
    }
}
