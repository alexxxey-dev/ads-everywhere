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