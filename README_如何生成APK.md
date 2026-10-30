# 锁屏打字提醒 (TypeReminder) - Android 原生工程包

本工程为您定制了**极简锁屏打字提醒 APP** 的完整 Android 原生源码：
- **核心功能**：设定时间与提醒文字 -> 到点通过系统 `AlarmManager` 精准唤醒锁屏 -> 在锁屏界面以**逐字打字机动画 + 震动反馈**敲出您的日程提醒。
- **极简操作**：单页面设计，无广告、无多余菜单。

---

## 方式一：免安装软件，用 GitHub 自动云端打包 APK（推荐，仅需 2 分钟）

如果您电脑上没有安装 Android Studio，可以直接用 GitHub 免费云端服务器自动编译生成 `.apk` 文件：

1. 登录 [GitHub](https://github.com/) 并点击右上角 **+** -> **New repository** 创建一个新仓库（比如命名为 `lockscreen-reminder`）。
2. 点击页面上的 **uploading an existing file**，将本压缩包解压后的所有文件和文件夹（包含 `.github` 文件夹）拖入上传，点击底部 **Commit changes**。
3. 点击仓库顶部的 **Actions** 标签页，您会看到 `Build Android APK` 正在自动运行。
4. 等待约 1~2 分钟变成绿色勾号 `✔` 后，点击进入该任务，在页面底部的 **Artifacts** 区域点击 **`LockScreenReminder-APK`** 即可下载编译好的直接安装版 `.apk` 文件！

---

## 方式二：使用 Android Studio 本地一键生成 APK

1. 打开 **Android Studio** -> 选择 **Open** -> 选择本解压目录。
2. 等待 Gradle 自动同步完成。
3. 点击顶部菜单栏 **Build** -> **Build Bundle(s) / APK(s)** -> **Build APK(s)**。
4. 编译完成后点击右下角弹窗的 **locate**，即可拿到 `app-debug.apk` 发送到手机直接安装。

> **手机端权限小贴士**：安装后首次使用时，请在系统设置中允许本应用的**“锁屏显示 / 后台弹出界面”**与**“闹钟和提醒”**权限，确保手机在息屏锁屏状态下能自动亮屏打字。
