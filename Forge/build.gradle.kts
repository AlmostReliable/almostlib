val minecraftVersion: String by project
val modId: String by project
val forgeVersion: String by project

val extraModsPrefix = "extra-mods"

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    if (project.findProperty("enableAccessWidener") == "true") { // optional property for `gradle.properties`
        accessWidenerPath.set(project(":Common").loom.accessWidenerPath)
        forge {
            convertAccessWideners.set(true)
            extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
        }
        println("Access widener enabled for project ${project.name}. Access widener path: ${loom.accessWidenerPath.get()}")
    }

    // load the test mod manually because Forge always uses main by default
    mods {
        create("testmod") {
            sourceSet(sourceSets.test.get())
            sourceSet(project(":Common").sourceSets.test.get())
        }
    }

    forge {
        mixinConfigs("$modId-common.mixins.json" /*, "$modId-forge.mixins.json"*/)
    }
}

repositories {
    flatDir {
        name = extraModsPrefix
        dir(file("$extraModsPrefix-$minecraftVersion"))
    }
}

val common by configurations
val shadowCommon by configurations

dependencies {
    // loader
    forge("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    // common module
    common(project(":Common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":Common", "transformProductionForge")) { isTransitive = false }
    testImplementation(project(":Common", "namedElements"))

    /**
     * helps to load mods in development through an extra directory
     * sadly, this does not support transitive dependencies
     */
    fileTree("$extraModsPrefix-$minecraftVersion") { include("**/*.jar") }
        .forEach { f ->
            val sepIndex = f.nameWithoutExtension.lastIndexOf('-')
            if (sepIndex == -1) {
                throw IllegalArgumentException("Invalid mod name: '${f.nameWithoutExtension}'. Expected format: 'modName-version.jar'")
            }
            val mod = f.nameWithoutExtension.substring(0, sepIndex)
            val version = f.nameWithoutExtension.substring(sepIndex + 1)
            println("Extra mod ${f.nameWithoutExtension} detected.")
            "modLocalRuntime"("extra-mods:$mod:$version")
        }
}
