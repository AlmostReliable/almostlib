val minecraftVersion: String by project
val fabricLoaderVersion: String by project
val fabricApiVersion: String by project
val rebornEnergyVersion: String by project

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    if (project.findProperty("enableAccessWidener") == "true") { // optional property for `gradle.properties`
        accessWidenerPath.set(project(":Common").loom.accessWidenerPath)
        println("Access widener enabled for project ${project.name}. Access widener path: ${loom.accessWidenerPath.get()}")
    }

    // create test implementations that allow remapping
    createRemapConfigurations(sourceSets.getByName("test"))
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

    // Reborn Energy
    modCompileOnly("teamreborn:energy:$rebornEnergyVersion")
    "modTestRuntimeOnly"("teamreborn:energy:$rebornEnergyVersion")
}

/**
 * force the fabric loader and api versions that are defined in the project
 * some mods ship another version which crashes the runtime
 */
configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:$fabricLoaderVersion")
        force("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion+$minecraftVersion")
    }
}
