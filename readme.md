## Initialization 
### settings.gradle
```groovy
dependencyResolutionManagement {
    repositories {
        maven {
            name = "GitHubPackages"
            url = "https://maven.pkg.github.com/alexxxey-dev/ads-everywhere"
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
    implementation("com.alexxxey.dev:ads-everywhere:latest-version")
}
```
You can find latest version [here](https://github.com/alexxxey-dev/ads-everywhere/packages/2155117)