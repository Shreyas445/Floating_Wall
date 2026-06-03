# Floating Wall 🧱

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)

**Floating Wall** is a lightweight, privacy-focused Android utility that bypasses OEM hardware limitations by mapping a floating on-screen button to powerful system actions using a custom multi-tap engine.

## 🚀 The Problem It Solves
**This app is made for the person who had damaged their volume buttons**
**User can trigger volume, screenshots from Floating button.**
**Floating Wall** bypasses this by hijacking the native Android Accessibility button and injecting a custom time-delay engine to allow for 1-tap, 2-tap, and 3-tap physical macros—all without requiring Root access.

## ✨ Features
* **Multi-Tap Engine:** Configure different system actions for single, double, and triple taps.
* **Zero Battery Drain:** Built on an Event-Driven Architecture. The app sleeps entirely in the background until the physical button is pressed.
* **Snappy Haptics:** Millisecond-perfect vibration feedback creates the illusion of a tactile, physical button.
* **Premium UI:** iOS-style dark mode aesthetic with rounded cards and native dropdowns.
* **Privacy First:** Operates 100% locally. No internet permission required. No data collection.

## 🛠️ Current Supported Actions
* Force Open Volume Panel (without changing volume)
* Take Screenshot
* *(More actions coming soon)*

## 📸 Screenshots
*(Note to self: Take a screenshot of the dark mode UI and put it here)*
## ⚙️ Installation & Setup
1. Download the latest `.apk` from the [Releases](https://github.com/Shreyas445/Floating_Wall/releases) tab or clone this repository and build it via Android Studio.
2. Open the app and tap **Enable Accessibility**.
3. Toggle the **Floating Wall shortcut** ON to reveal the green floating button.
4. Select your preferred actions for 1, 2, and 3 taps in the app dashboard.
5. Tap **Optimize Battery** and allow the exemption so your phone's OS doesn't kill the service in the background.

## 💻 Tech Stack
* **Language:** Kotlin
* **Core APIs:** `AccessibilityService`, `AccessibilityButtonController`, `AudioManager`, `Vibrator`
* **Data Storage:** `SharedPreferences`
* **UI:** Native XML with Material Components

## 🤝 Contributing
Since this was built to help people with broken volume rockers or locked OEM hardware, contributions are highly welcome!
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📜 License
Distributed under the MIT License. See `LICENSE` for more information.