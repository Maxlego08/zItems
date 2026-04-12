pluginManagement {
    repositories {
        maven {
            name = "groupezReleases"
            url = uri("https://repo.groupez.dev/releases")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "zItems"
include("api")
include("common")

file("hooks").listFiles()?.forEach { file ->
    if (file.isDirectory and !file.name.equals("build")) {
        println("Include hooks:${file.name}")
        include(":hooks:${file.name}")
    }
}

file("versions").listFiles()?.forEach { file ->
    if (file.isDirectory && !file.name.equals("build")) {
        println("Include versions:${file.name}")
        include(":versions:${file.name}")
    }
}