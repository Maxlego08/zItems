group = "Hooks:ZJobs"

repositories {

}

dependencies {
    compileOnly(projects.api)
    compileOnly(files("libs/zJobs-1.0.0.jar"))
}