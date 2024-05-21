## SDK Initialization 
### settingsTemplate.gradle
```groovy
dependencyResolutionManagement {
    repositories {
        maven {
            name "GitHubPackages"
            url "https://maven.pkg.github.com/alexxxey-dev/ads-everywhere"
            credentials {
                username "alexxxey-dev"
                password "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"
            }
        }
    }
}
```
### mainTemplate.gradle
```groovy
dependencies {
    implementation("com.alexxxey.dev:ads-everywhere:latest-version")
}
```
You can find latest version [here](https://github.com/alexxxey-dev/ads-everywhere/packages/2155117)
### AndroidManifest.xml
Replace default Unity Player Activity (android:name attribute) with com.ads.everywhere.ui.UnityPlayerActivity
```xml
 <activity android:name="com.ads.everywhere.ui.UnityPlayerActivity"
             android:theme="@style/UnityThemeSelector"
             android:label="@string/app_name"
             android:configChanges="fontScale|keyboard|keyboardHidden|locale|mnc|mcc|navigation|orientation|screenLayout|screenSize|smallestScreenSize|uiMode|touchscreen">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
```
## SDK Usage
```kotlin
/**
    JavaObject com.ads.everywhere.AdsEverywhere(context)
 **/
interface AdsEverywhere {

    /**
        Initialize SDK on every application launch
     **/
    fun init()

    /**
        Check if user granted all permissions
     **/
    fun hasPermissions():Boolean

    /**
        Request user to grant permissions
     **/
    fun requestPermissions()

    /**
     Call when user navigates to screen with reward button
     **/
    fun onRewardScreen()
}
```