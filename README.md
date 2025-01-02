# CandyBar

[![](https://jitpack.io/v/SamNeill/Candybar.svg)](https://jitpack.io/#SamNeill/Candybar)
[![Android CI](https://github.com/SamNeill/Candybar/actions/workflows/android.yml/badge.svg)](https://github.com/SamNeill/Candybar/actions/workflows/android.yml)

A modern and feature-rich icon pack dashboard based on [CandyBar Library](https://github.com/zixpo/candybar-sample), with enhanced Material You support and modern Android features.

This project is a fork of the original [CandyBar Library](https://github.com/zixpo/candybar-sample) created by [Dani Mahardhika](https://github.com/danimahardhika) and maintained by [Sarsa Murmu](https://github.com/zixpo).

## Navigation Options
- **Sidebar Navigation**: A classic Material Design navigation drawer that slides in from the left, providing easy access to all sections and settings. Perfect for apps with many navigation items.
- **Bottom Navigation**: A modern bottom bar navigation style that follows Material You design guidelines. Ideal for one-handed use and a more contemporary feel.

## Installation

Add JitPack repository to your root `build.gradle`:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency:
```gradle
dependencies {
    implementation 'com.github.SamNeill:Candybar:Latest'
}
```

## Features
- Material You design support
- License checker
- Apply icons directly in supported launchers
- Icon picker
- Icon request system
  - Regular requests
  - Premium requests
- Cloud based wallpapers
  - Preview wallpaper
  - Apply wallpaper
  - Download wallpaper
- Settings
  - Clear cache
  - Theme switching (Auto/Light/Dark/Material You)
  - Restore purchases
- Search-enabled FAQ section
- About section
- Donation support
- Changelog display on updates
- Muzei live wallpaper support
- Kustom preset support
- Multi-language support
- OneSignal notifications
- Update checker
- Remote dashboard configuration via JSON
- Advanced configuration API

## Requirements
- Android Studio Arctic Fox (2020.3.1) or higher
- Android 12 (SDK 31) or higher
- Gradle 7.0.0 or higher
- JDK 17

## Credits
- Original CandyBar Library by [Dani Mahardhika](https://github.com/danimahardhika)
- Current CandyBar maintainer [Sarsa Murmu](https://github.com/zixpo)
- [All CandyBar contributors](https://github.com/zixpo/candybar-sample/graphs/contributors)

## License
```
Copyright (c) 2014-2016 Dani Mahardhika

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
