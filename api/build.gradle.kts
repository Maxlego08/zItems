plugins {
    id("re.alwyn974.groupez.publish") version "1.0.0"
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
}

rootProject.extra.properties["sha"]?.let { sha ->
    version = sha
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    shadowJar {
        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }

    javadoc {
        options.encoding = "UTF-8"
        if (JavaVersion.current().isJava9Compatible)
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}