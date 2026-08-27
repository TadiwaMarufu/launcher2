# EmoLauncher

<p align="center">
  <b>MAXIMUM CUSTOMIZATION • MINIMUM FRICTION • MAXIMUM CAPABILITY • MINIMUM VISIBLE CLUTTER</b>
</p>

---

## 🌟 Overview

**EmoLauncher** is a production-ready, open-source Android user experience layer crafted with Kotlin and Jetpack Compose. It is engineered to give users the sensation of having a completely customized operating system while operating as a normal Android launcher that requires **zero system modification or root privileges**.

The visual and functional design language fuses:
* **Smart Launcher Fluidity & Intuitiveness**
* **Physical Liquid Glass Material Layer** (dynamic specular refraction, adjustable blur, custom edge highlights)
* **Modern Material You & Dynamic Android 12+ Color Ingestion**
* **Unix / Linux Developer Minimalism & Embedded Shell**
* **Deep OLED Near-Black (#05090D, #000000) Power Efficiency**
* **Gemini 3.5 Flash Grounded AI Search & Natural Language Command Palette**

---

## 🚀 Key Features

### 1. Radically Minimal Home Screen Canvas
- **Customizable Terminal Prompt Header**: Live interactive `┌─(emo㉿kali)-[~]\n└─$` header with system status indicators, custom user/host styling, and instant terminal launch.
- **Precision Orbital Clock Engine**: Customizable clock supporting Orbital Circles with traveling second dots, Minimal Text, Unix Timestamp, Vertical numerals, Bold Digital, and Hybrid Analog.
- **Live System Telemetry HUD**: Real-time monitoring of CPU, RAM, Battery %, Storage GB, and Uptime.
- **Contextual Music Player Widget**: Auto-collapsing player widget with playback controls, track progress, and album metadata.
- **Adaptive Dock System**: Supports 0 to 7 applications with Liquid Glass, Floating Pill, or Transparent styles.

### 2. Universal Search & Developer Command Palette
- **Unified Querying**: Instantly filter installed applications, execute math calculations (`calc 128*4`), and run system commands (`> wifi`, `> bluetooth`, `> flash`, `> battery`, `> restart`, `> clean`).
- **Gemini Search Grounding**: Ask natural language questions directly in the search bar and receive concise, live-grounded answers powered by Google Gemini 3.5 Flash.

### 3. Integrated Unix Terminal Shell (`emo-sh`)
- Built-in lightweight terminal supporting commands: `help`, `apps`, `ls`, `launch <app>`, `neofetch`, `sysinfo`, `wifi`, `battery`, `calc`, `ai <prompt>`, and `clear`.

### 4. Smart Liquid Glass App Drawer
- **Intelligent Auto-Categorization**: Dev, Tools, Media, Social, System, Games, Starred, and Private categories.
- **Contextual App Control**: Long-press any app to pin/unpin from Home or Dock, rename shortcuts, view app details, hide applications, or uninstall.

### 5. Customization Center & Live Preview Engine
- Real-time preview card reflecting instant changes across themes, clock typography, Liquid Glass opacity and blur, icon scales, wallpapers, gestures, and backup/restore JSON configurations.

---

## 🛠 Tech Stack

- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Local Persistence:** Room Database & SharedPreferences
- **AI Integration:** Google Gemini API (`gemini-3.5-flash`) with Google Search Grounding
- **Build System:** Gradle Kotlin DSL (`.gradle.kts`) with Version Catalog

---

## 📄 Open Source & Privacy

- **Zero Telemetry:** EmoLauncher operates 100% on-device. No telemetry, user profiling, or background tracking.
- **License:** MIT License. Free and open source for everyone.
