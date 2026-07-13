# 🎧 DAC KeepAlive for Android TV

A native and lightweight solution to fix compatibility issues and audio artifacts ("popping" or clicking noises) when using USB audio DACs and sound systems on Smart TVs running Android TV or Google TV (such as TCL, Sony, etc.).

## ⚠️ The Problem
When navigating through the TV menus, Android cuts the audio stream to save resources (putting the USB port on *standby*). When a UI navigation sound is played, the DAC wakes up abruptly, generating an annoying electrical pop/click sound in the speakers.

## 💡 The Solution
**DAC KeepAlive** runs a Foreground Service that sends a continuous stream of absolute silence (binary zeros) to the USB port.
This tricks the operating system, preventing both the USB port and the DAC from entering sleep/standby mode. It keeps the audio channel permanently open without interfering with your movie or music playback.

## ✨ Features
* **Auto-Start:** Starts automatically as soon as the TV is turned on (no need to open the app manually every time).
* **Ultra-Lightweight:** The silent audio stream runs at 8000Hz Mono, consuming virtually **0% CPU**.
* **Background Execution:** Runs completely silently in the background once activated.
* **Modern UI:** Simple activation interface built with **Jetpack Compose**.

## 🛠️ Tech Stack
* **Language:** Kotlin
* **UI:** Jetpack Compose
* **Audio:** `AudioTrack` API (PCM 16-bit)
* **Architecture:** Android Foreground Services & Broadcast Receivers

## 📦 How to Install (Sideload)
Since this is a system utility app, it must be installed via sideloading:
1. Download the `.apk` file from the [Releases] tab in this repository.
2. Transfer the APK to a USB flash drive and plug it into your TV (or push it via ADB).
3. Use a File Manager on your TV to install the application.
4. **Important:** Open the application on your TV at least *once* and click on "Start Service". This is required so Android allows the app to start automatically the next time you turn on the TV.

## 🤝 Contributing
Feel free to open *Issues* or submit *Pull Requests* if you have ideas to improve the code or add support for specific behaviors of other TV models.