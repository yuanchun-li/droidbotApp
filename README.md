# droidbotApp
An app runing on Android to coordinate with DroidBot.

## Installation

1. `git clone` this project to your local storage.
2. Import [PrivacyStreams Android SDK](https://github.com/PrivacyStreams/PrivacyStreams) into this project.
3. Modify `io/github/privacystreams/accessibility/PSAccessibilityService.java` in privacystreams-android-sdk according to `io/github/ylimit/droidbotapp/PSAccessibilityServicePatch.java` in this module.
4. Move necessary library jars from app/libs to PrivacyStreams-Android-SDK/libs.
5. Build app. Done.

