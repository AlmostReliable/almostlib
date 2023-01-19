val minecraftVersion: String by project
val fabricLoaderVersion: String by project
val fabricApiVersion: String by project

plugins {
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    if (project.findProperty("enableAccessWidener") == "true") { // Optional property for `gradle.properties` to enable access wideners.
        accessWidenerPath.set(project(":Common").loom.accessWidenerPath)
        println("Access widener enabled for project ${project.name}. Access widener path: ${loom.accessWidenerPath.get()}")
    }
}

val common by configurations
val shadowCommon by configurations
dependencies {
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modApi("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion+$minecraftVersion")

    common(project(":Common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":Common", "transformProductionFabric")) { isTransitive = false }
}
