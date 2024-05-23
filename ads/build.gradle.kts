import org.gradle.internal.impldep.bsh.commands.dir
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

val githubProperties = Properties().apply {
    load(FileInputStream(rootProject.file("github.properties")))
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("release") {
        groupId = "com.alexxxey.dev"
        artifactId = "ads-everywhere"
        version = "1.0.1"
        artifact("$buildDir/outputs/aar/ads-release.aar")

        pom.withXml {
            val dependenciesNode = asNode().appendNode("dependencies")

            configurations.implementation.get().dependencies.forEach{ dependency ->
                if (dependency.group == null ||
                    dependency.version == null ||
                    dependency.name == null ||
                    dependency.name == "unspecified") {
                    return@forEach
                }

                dependenciesNode.appendNode("dependency").apply {
                    appendNode("groupId", dependency.group)
                    appendNode("artifactId", dependency.name)
                    appendNode("version", dependency.version)
                }
            }

        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/alexxxey-dev/ads-everywhere")
            credentials {
                username = githubProperties["gpr.usr"] as String
                password = githubProperties["gpr.key"] as String
            }
        }
    }
}

android {
    namespace = "com.ads.everywhere"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        targetSdk = 34
        version = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")



    //analytics
    implementation("io.appmetrica.analytics:analytics:6.2.1")
    //gson
    implementation("com.google.code.gson:gson:2.10.1")

    //navigation
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    //koin
    implementation("io.insert-koin:koin-core:3.4.2")
    implementation("io.insert-koin:koin-android:3.4.2")



}

