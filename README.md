# Apex QMS — Industrial Quality Management System

Apex QMS is a native Android application built with **Kotlin** and **Jetpack Compose**, specifically engineered for industrial quality control, live fleet telemetry, and roll audit workflows in high-output flexible packaging manufacturing plants.

---

## ⚠️ Note on GitHub Pages (Resolving 404 / Blank Page)

If you enabled **GitHub Pages** on this repository and saw a **`404 File Not Found`** error:

> **Root Cause:** GitHub Pages is a static web hosting server that serves web pages via an `index.html` file. Because this project is a native **Android application** (not a website), it consists of Android Kotlin code (`.kt`), Gradle files, and Android resources.

### What We Added:
1. **`index.html`** in the repository root: Provides an instant, branded **Apex QMS Web Portal & Telemetry Overview** with instructions on downloading and running the app. When pushed to GitHub, GitHub Pages will automatically serve this portal with 0 configuration.
2. **`.nojekyll`**: Disables Jekyll processing on GitHub Pages to ensure all static assets load properly.
3. **`.github/workflows/deploy-pages.yml`**: Automatically deploys the portal to GitHub Pages on every push.
4. **`.github/workflows/build-apk.yml`**: Automatically compiles the Android `.apk` package in GitHub Actions and makes it downloadable under the **Actions > Artifacts** tab.

---

## How to Run & Test the Android App

### 1. In Google AI Studio
- Open the project in **Google AI Studio**.
- The native app runs live in the interactive **Streaming Android Emulator** on the right side.

### 2. In Android Studio (Local Development)
1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/<your-repo>.git
   cd <your-repo>
   ```
2. Open Android Studio and select **Open**, then choose this project folder.
3. Allow Gradle to sync.
4. Connect an Android phone via USB (with Developer options & USB debugging enabled) or launch an Android Virtual Device (AVD).
5. Click **Run (`Shift + F10`)**.

### 3. Build & Install the APK via Command Line
Run Gradle to assemble the debug APK:
```bash
# On Linux / macOS:
./gradlew assembleDebug

# On Windows:
gradlew.bat assembleDebug
```
The APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

Install directly on your connected device:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Core Modules & Features

- **Fleet Telemetry Monitor**: Bird's-eye view of extrusion lines, rotogravure presses, and slitter rewinders with live status indicators, film thickness gauges, and quarantine controls.
- **Roll Audit & Verification**: Quality sign-off checklist for ASTM/ISO packaging standards (ASTM D882 tensile yield, tear resistance, corona treatment dynes).
- **Defect & NCR Logging**: Non-conformance reporting with root cause analysis (5-Why), containment actions, scrap footage tracking, and severity categorization.
- **Lab Test Records**: Historic batch records, COF measurements, and certificates of analysis.

---

## Tech Stack
- **OS**: Android (minSdk 24, targetSdk 36)
- **UI Toolkit**: Jetpack Compose & Material 3 (M3)
- **Architecture**: MVVM with Kotlin Coroutines & `StateFlow`
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`)
