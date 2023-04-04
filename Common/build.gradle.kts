val enabledPlatforms: String by project
val modPackage: String by project
val modId: String by project
val modName: String by project
val fabricLoaderVersion: String by project

plugins {
    id("com.github.gmazzo.buildconfig") version "4.0.4"
}

architectury {
    common(enabledPlatforms.split(","))
}

loom {
    if (project.findProperty("enableAccessWidener") == "true") { // optional property for `gradle.properties`
        accessWidenerPath.set(file("src/main/resources/$modId.accesswidener"))
        println("Access widener enabled for project ${project.name}. Access widener path: ${loom.accessWidenerPath.get()}")
    }
}

dependencies {
    /**
     * loader
     * required here for the @Environment annotations and the mixin dependencies
     * do NOT use other classes from the Fabric loader
     */
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    implementation("com.electronwill.night-config:toml:3.6.0")
    include("com.electronwill.night-config:toml:3.6.0")
}

buildConfig {
    buildConfigField("String", "MOD_ID", "\"$modId\"")
    buildConfigField("String", "MOD_NAME", "\"$modName\"")
    buildConfigField("String", "MOD_VERSION", "\"$version\"")
    packageName(modPackage)
    className("AlmostLibConstants")
    useJavaOutput()
}
