## Initialization 
### settings.gradle
```groovy
dependencyResolutionManagement {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/alexxxey-dev/offerwall")
            credentials {
                username = "alexxxey-dev"
                password = "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"
            }
        }
    }
}
```
### build.gradle (app)
```groovy
dependencies {
    implementation("com.apptower.sdk:offerwall:latest-version")
}
```
You can find latest version [here](https://github.com/alexxxey-dev/offerwall/packages/2150211)