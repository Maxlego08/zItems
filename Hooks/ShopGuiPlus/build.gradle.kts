group = "Hooks:ShopGuiPlus"

repositories {

}

dependencies {
    compileOnly(projects.api)
    compileOnly(files("libs/shopgui-api-3.0.0.jar"))
}