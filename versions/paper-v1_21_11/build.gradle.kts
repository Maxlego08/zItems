// Paper 1.21.11 module (Mounts of Mayhem) — 1.21.11+ specific implementations.
// New data components: ATTACK_RANGE, KINETIC_WEAPON, PIERCING_WEAPON.
configurations.all {
    exclude(group = "org.spigotmc", module = "spigot-api")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly(project(":api"))
    compileOnly(project(":common"))
}