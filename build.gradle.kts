plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("re.alwyn974.groupez.repository") version "1.0.0"
}

apply("gradle/copy-build.gradle")

extra.set("targetFolder", file("target/"))
extra.set("apiFolder", file("target-api/"))
extra.set("classifier", System.getProperty("archive.classifier"))
extra.set("sha", System.getProperty("github.sha"))

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "re.alwyn974.groupez.repository")

    group = "fr.traqueur"
    version = "1.0.0"

    repositories {
        mavenCentral()

        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven(url = "https://jitpack.io")
    }

    tasks.shadowJar {
        archiveBaseName.set("zItems")
        archiveAppendix.set(if (project.path == ":") "" else project.name)
        archiveClassifier.set("")

        relocate("fr.traqueur.structura", "fr.traqueur.items.libs.structura")
        relocate("fr.traqueur.commands", "fr.traqueur.items.libs.commands")
        relocate("fr.traqueur.recipes", "fr.traqueur.items.libs.recipes")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
        /* Depends */
        compileOnly("me.clip:placeholderapi:2.11.6")
        compileOnly("fr.maxlego08.menu:zmenu-api:1.1.0.4")
        compileOnly(files(rootProject.files("libs/zMenu-1.1.0.4.jar")))

        /* Adventure for Spigot compatibility */
        compileOnly("net.kyori:adventure-platform-bukkit:4.3.4")
        compileOnly("net.kyori:adventure-text-minimessage:4.18.0")

        /* Libraries */
        implementation("com.github.Traqueur-dev:RecipesAPI:3.1.1")
        implementation("fr.traqueur:structura:1.6.0")
        implementation("com.github.Traqueur-dev.CommandsAPI:platform-spigot:4.2.3")
        compileOnly("org.reflections:reflections:0.10.2")

    }

}

dependencies {
    api(project(":api"))
    implementation(project(":common"))

    rootProject.subprojects.filter { it.path.startsWith(":hooks:") }.forEach { subproject ->
        implementation(project(subproject.path))
    }
}

tasks {
    shadowJar {
        rootProject.extra.properties["sha"]?.let { sha ->
            archiveClassifier.set("${rootProject.extra.properties["classifier"]}-${sha}")
        } ?: run {
            archiveClassifier.set(rootProject.extra.properties["classifier"] as String?)
        }
        destinationDirectory.set(rootProject.extra["targetFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
        dependsOn(subprojects.map { it.tasks.shadowJar })
    }

    processResources {
        from("resources")
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}