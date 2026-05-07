import java.util.Properties

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

        maven {
            name = "GitHubPackagesTangem"
            url = uri("https://maven.pkg.github.com/tangem/tangem-sdk-android")
            credentials {
                val props = Properties()
                val localPropsFile = File(rootDir, "local.properties")
                if (localPropsFile.exists()) {
                    props.load(localPropsFile.inputStream())
                }

                username = props.getProperty("gpr.user", System.getenv("GITHUB_USER"))
                password = props.getProperty("gpr.key", System.getenv("GITHUB_TOKEN"))
            }
        }
    }
}
include(":app", ":pinlockview", ":tsmapper")