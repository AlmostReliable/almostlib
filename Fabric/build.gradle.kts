import net.fabricmc.loom.task.RemapJarTask

val minecraftVersion: String by project
val fabricLoaderVersion: String by project
val fabricApiVersion: String by project
val teamRebornEnergyApiVersion: String by project

plugins {
    id("com.github.johnrengelman.shadow") version ("8.1.1")
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
    // loader
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modApi("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion+$minecraftVersion")

    // common module
    common(project(":Common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":Common", "transformProductionFabric")) { isTransitive = false }
    testImplementation(project(":Common", "namedElements"))

//    // Team Reborn Energy API.
//    modApi("teamreborn:energy:$teamRebornEnergyApiVersion") {
//        exclude(group = "net.fabricmc", module = "fabric-api")
//    }
}

tasks {
    // allow discovery of AWs from dependencies
    named<RemapJarTask>("remapJar") {
        injectAccessWidener.set(true)
    }
}
