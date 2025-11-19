pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
//dependencyResolutionManagement {
//    repositories {
//        maven(url = "https://zebratech.jfrog.io/artifactory/EMDK-Android/")
//    }
//}
rootProject.name = "ZebraKotlinDemo"
include(":app")
include(":emdk_kotlin_wrapper")
