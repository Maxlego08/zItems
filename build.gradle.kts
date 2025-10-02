plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("re.alwyn974.groupez.repository") version "1.0.0"
}

group = "fr.maxlego08.items"
version = "1.0.0"

extra.set("targetFolder", file("target/"))
extra.set("apiFolder", file("target-api/"))
extra.set("classifier", System.getProperty("archive.classifier"))
extra.set("sha", System.getProperty("github.sha"))

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "re.alwyn974.groupez.repository")

    group = "fr.maxlego08.items"
    version = rootProject.version

    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://jitpack.io")
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven(url = "https://libraries.minecraft.net/")
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.shadowJar {
        archiveBaseName.set("zItems")
        archiveAppendix.set(if (project.path == ":") "" else project.name)
        archiveClassifier.set("")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
        if (JavaVersion.current().isJava9Compatible)
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
        compileOnly("com.mojang:authlib:1.5.26")
        compileOnly("me.clip:placeholderapi:2.11.6")

        compileOnly("fr.maxlego08.menu:zmenu-api:1.1.0.4")
        compileOnly("fr.maxlego08.essentials:zessentials-api:1.0.2.7")
        compileOnly(files(rootProject.files("libs/zMenu-1.1.0.4.jar")))
        implementation("com.github.Traqueur-dev:RecipesAPI:3.0.0")
        implementation("com.jeff-media:armor-equip-event:1.0.3")
    }
}

repositories {

}

dependencies {
    api(project(":API"))

    rootProject.subprojects.filter { it.path.startsWith(":Hooks:") }.forEach { subproject ->
        api(project(subproject.path))
    }
}

tasks {
    shadowJar {
        relocate("fr.traqueur.recipes", "fr.maxlego08.items.hooks.recipes")
        relocate("com.jeff_media.armorequipevent", "fr.maxlego08.items.hooks.armorequipevent")

        rootProject.extra.properties["sha"]?.let { sha ->
            archiveClassifier.set("${rootProject.extra.properties["classifier"]}-${sha}")
        } ?: run {
            archiveClassifier.set(rootProject.extra.properties["classifier"] as String?)
        }
        destinationDirectory.set(rootProject.extra["targetFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        // Configuration already set in allprojects block
    }

    processResources {
        from("resources")
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}