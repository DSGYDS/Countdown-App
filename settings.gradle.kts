pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/google/android") }
        maven { url = uri("https://maven.aliyun.com/public") }
        maven { url = uri("https://maven.aliyun.com/gradle/plugin") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/google/android") }
        maven { url = uri("https://maven.aliyun.com/public") }
        google()
        mavenCentral()
    }
}

rootProject.name = "CountdownApp"
include(":app")