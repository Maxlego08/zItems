group = "Hooks:Jobs"

repositories {

}

dependencies {
    compileOnly(projects.api)
    compileOnly(files("libs/Jobs5.2.6.1.jar"))
}