group = "Hooks:WorldGuard"

repositories {
    mavenCentral()
    maven(url = "https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly(projects.api)
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.14")
    // compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.14")
}