group = "Hooks:SuperiorSkyBlock2"

repositories {
    mavenCentral()
    maven(url = "https://repo.bg-software.com/repository/api/")
}

dependencies {
    compileOnly(projects.api)
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2025.1")
}