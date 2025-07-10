plugins {
    id("re.alwyn974.groupez.publish") version "1.0.0"
}

rootProject.extra.properties["sha"]?.let { sha ->
    version = sha
}

dependencies {
    compileOnly("fr.maxlego08.menu:zmenu-api:1.1.0.1")
}

tasks {
    shadowJar {
        relocate("fr.traqueur.recipes", "fr.maxlego08.items.hooks.recipes")
        relocate("com.jeff_media.armorequipevent", "fr.maxlego08.items.hooks.armorequipevent")

        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }
}

publishConfig {
    githubOwner.set("MaxLego08")
}
