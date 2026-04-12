// Paper 1.21.4 base module — all Paper-specific implementations for 1.21.4+.
// Excludes spigot-api from allprojects since paper-api provides the same capability.
configurations.all {
    exclude(group = "org.spigotmc", module = "spigot-api")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
    compileOnly(project(":common"))
}