# runtime-android

Placeholder Android host. It is a **plain JVM library** in this skeleton so CI does not need the Android SDK.

Later work (tracked by issues, not this commit):

1. Switch the module to the Android Gradle Plugin.
2. Embed or speak to a J2ME-compatible host, or draw with a modern Android view.
3. Produce an APK that loads the game from app storage. Confirm the redistribution authorization covers APK embedding before bundling it.

Until then, play on a phone with the original JAR plus [J2ME Loader](https://github.com/nikita36078/J2ME-Loader).
