# Nebula 倒计时中心

全新架构的 JavaFX 倒计时应用，采用模块化依赖注入 + MVVM 设计，以更具审美的 UI 与更强大的功能打造沉浸式的时间管理体验。

## 主要特性

- 🎨 **双主题高颜值界面**：内置日间 / 夜间模式，可随时切换，配合渐变背景、光晕与卡片化布局，呈现现代设计语言。
- 🧠 **MVVM 架构重塑**：独立的 `TimerService`、`SoundService`、`ThemeManager` 等服务模块，通过 `CountdownViewModel` 与界面解耦，逻辑更清晰、扩展更容易。
- ⏱️ **专业计时能力**：支持开始、暂停、继续、复位等完整控制流程，并实时显示预计完成时间与进度条。
- ⚡ **高效预设管理**：内置番茄钟、短休息、深度工作等多个预设，一键加载到输入面板，快速进入专注状态。
- 🔔 **完成提醒**：倒计时结束自动播放提示音，确保重要时刻不错过。

## 项目结构

```
src/
 └─ main/
     ├─ java/
     │   └─ com/zen/timer/
     │       ├─ app/              # 应用入口与依赖容器
     │       ├─ model/            # 领域模型（状态、预设）
     │       ├─ service/          # 业务服务（计时、主题、声音）
     │       ├─ util/             # 公共工具
     │       ├─ view/             # UI 视图层
     │       └─ viewmodel/        # MVVM 视图模型
     └─ resources/
         └─ com/zen/timer/styles/ # JavaFX 样式资源
```

## 运行方式

确保已正确配置 JavaFX 模块路径后运行主类，例如：

```bash
javac --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.graphics -d out \
    $(find src/main/java -name "*.java")
java --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.graphics \
    -cp out com.zen.timer.app.CountdownTimerApp
```

如使用 Maven/Gradle，可在各自的构建脚本中引入 JavaFX 依赖后执行 `com.zen.timer.app.CountdownTimerApp` 主类。

## 截图

> 可在运行程序后，使用系统截图工具捕获日间/夜间主题的效果。

## 许可证

本项目遵循 [MIT License](LICENSE)。
