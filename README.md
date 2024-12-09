# CandyBar Amazon Icon Pack Dashboard

[![JitPack](https://jitpack.io/v/SamNeill/Candybar-Amazon.svg)](https://jitpack.io/#SamNeill/Candybar-Amazon)
[![Apache2 License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A customized version of the CandyBar Dashboard library specifically configured for Amazon Appstore icon packs. This fork includes Amazon In-App Billing integration and is optimized for publishing icon packs on the Amazon Appstore.

## Features

- Full Amazon Appstore compatibility
- Integrated Amazon In-App Billing
- Material Design dashboard
- Icon request tool
- Cloud-based wallpaper support
- Muzei Live Wallpaper support
- In-app icon preview
- Supports adaptive icons
- Easy icon request tool

## Project Structure

```
candybar-3.20.3.Amazon/
├── app/                    # Main application module
├── gradle/                 # Gradle wrapper directory
├── .github/               # GitHub specific files
├── build.gradle           # Project level build file
├── settings.gradle        # Project settings
├── gradle.properties      # Gradle configuration properties
├── jitpack.yml           # JitPack configuration
└── crowdin.yml           # Crowdin translation configuration
```

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- JDK 11 or newer
- Android SDK with minimum API 21
- Gradle 8.5.2 or newer

### Installation

1. Add JitPack repository to your build file:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency:

```gradle
dependencies {
    implementation 'com.github.SamNeill.Candybar-Amazon:candybar:v3.20.3-amazon'
    implementation 'com.github.SamNeill.Candybar-Amazon:PreLollipopTransitions:v3.20.3-amazon'
}
```

### Configuration

1. Update your app's `build.gradle`:
   - Set compileSdkVersion to match the library
   - Configure Amazon-specific settings

2. Configure dashboard:
   - Customize strings in `res/values/strings.xml`
   - Set up icon pack configuration in `res/values/dashboard_configurations.xml`

## Building

1. Clone the repository:
```bash
git clone https://github.com/SamNeill/Candybar-Amazon.git
```

2. Open in Android Studio

3. Sync project with Gradle files

4. Build the project:
```bash
./gradlew build
```

## Publishing

1. Configure your Amazon Developer account
2. Set up app signing
3. Configure in-app products in the Amazon Developer Console
4. Build and submit your app

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Credits and Attribution

This project is a fork of the [CandyBar Dashboard Library](https://github.com/danimahardhika/candybar-library) created by [Dani Mahardhika](https://github.com/danimahardhika). The original project provided the foundation for this Amazon-specific implementation.

### Special Thanks

- [Dani Mahardhika](https://github.com/danimahardhika) for creating and maintaining the original CandyBar Dashboard Library
- The entire CandyBar community for their contributions and support
- All contributors who have helped improve both the original and this Amazon fork
- Icon pack developers who have provided valuable feedback and testing

The original CandyBar project has been an invaluable resource for the Android icon pack community, and we are grateful for the opportunity to build upon this excellent foundation to create an Amazon Appstore specific version.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Original CandyBar Dashboard by Dani Mahardhika
- All contributors to the original CandyBar project
- Amazon Developer Platform

## Support

For support, please:
1. Check existing GitHub issues
2. Create a new issue
3. Join our community discussions

---
Made with ❤️ for the Amazon Appstore community
