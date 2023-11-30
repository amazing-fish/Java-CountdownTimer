import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class CountdownTimerModel {
    // remainingSeconds 是一个 LongProperty，用于跟踪剩余的秒数。
    private LongProperty remainingSeconds = new SimpleLongProperty();

    // remainingSecondsProperty 方法返回 remainingSeconds 属性。
    // 这允许其他类（例如控制器）监听这个属性的变化。
    public LongProperty remainingSecondsProperty() {
        return remainingSeconds;
    }

    // setRemainingSeconds 方法更新 remainingSeconds 属性的值。
    // 这用于设置倒计时的初始值或在倒计时过程中更新剩余时间。
    public void setRemainingSeconds(long remainingSeconds) {
        this.remainingSeconds.set(remainingSeconds);
    }

    // getRemainingSeconds 方法返回当前剩余的秒数。
    public long getRemainingSeconds() {
        return remainingSeconds.get();
    }

    // convertToSeconds 方法将小时、分钟和秒转换为总秒数。
    // 这用于从用户输入的时间计算出总秒数，以便开始倒计时。
    public long convertToSeconds(int hours, int minutes, int seconds) {
        return hours * 3600L + minutes * 60L + seconds;
    }

    // convertToHMS 方法将总秒数转换为小时:分钟:秒的格式。
    // 这用于在用户界面上以可读格式显示剩余时间。
    public String convertToHMS(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // updateCountdown 方法更新倒计时的剩余时间。
    // 每当调用此方法时，它会将剩余时间减少一秒，直到达到0。
    public void updateCountdown() {
        long currentValue = getRemainingSeconds();
        if (currentValue > 0) {
            setRemainingSeconds(currentValue - 1);
        }
    }

    // isCountdownFinished 方法检查倒计时是否结束。
    // 如果剩余秒数小于或等于0，则返回 true。
    public boolean isCountdownFinished() {
        return getRemainingSeconds() <= 0;
    }
}
