// Paper 1.21.5 module — code that requires 1.21.5+ Paper APIs.
// Excludes spigot-api from allprojects since paper-api provides the same capability.
configurations.all {
    exclude(group = "org.spigotmc", module = "spigot-api")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
    compileOnly(project(":common"))
}
