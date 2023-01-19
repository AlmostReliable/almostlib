val enabledPlatforms: String by project
val fabricLoaderVersion: String by project
val modId: String by project
val modName: String by project
val modPackage: String by project

plugins {
    id("com.github.gmazzo.buildconfig") version ("3.0.3")
}

architectury {
    common(enabledPlatforms.split(","))
}

loom {
    if (project.findProperty("enableAccessWidener") == "true") { // Optional property for `gradle.properties` to enable access wideners.
        accessWidenerPath.set(file("src/main/resources/$modId.accesswidener"))
        println("Access widener enabled for project ${project.name}. Access widener path: ${loom.accessWidenerPath.get()}")
    }
}

dependencies {
    // The Fabric loader is required here to use the @Environment annotations and to get the mixin dependencies.
    // Do NOT use other classes from the Fabric loader!
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
}

buildConfig {
    buildConfigField("String", "MOD_ID", "\"$modId\"")
    buildConfigField("String", "MOD_NAME", "\"$modName\"")
    buildConfigField("String", "MOD_VERSION", "\"$version\"")
    packageName(modPackage)
}
