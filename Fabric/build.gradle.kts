@file:Suppress("UnstableApiUsage")

plugins {
    idea
    `maven-publish`
    id("fabric-loom") version "1.0-SNAPSHOT"
}

val minecraftVersion: String by project
val fabricVersion: String by project
val fabricLoaderVersion: String by project
val modId: String by project
val mappingsChannel: String by project
val mappingsVersion: String by project
val sharedRunDir: String by project

loom {
    runs {
        onEach {
            it.configName = "Fabric ${it.environment.capitalize()}"
            it.ideConfigGenerated(true)
            it.runDir(if (sharedRunDir.toBoolean()) "../run" else "run")
            it.vmArgs("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition")
        }
    }

    mixin {
        defaultRefmapName.set("$modId.refmap.json")
    }
}


dependencies {
    /**
     * Core Mod Loader dependencies
     */
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation(project(":Common"))
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modApi("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:$mappingsChannel-$minecraftVersion:$mappingsVersion@zip")
    })

    /**
     * Non Minecraft dependencies
     */
    compileOnly("com.google.auto.service:auto-service:1.0.1")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    /**
     * Mod dependencies
     */
    fileTree("extra-mods-$minecraftVersion") { include("**/*.jar") }
        .forEach { f ->
            val sepIndex = f.nameWithoutExtension.lastIndexOf('-')
            if (sepIndex == -1) {
                throw IllegalArgumentException("Invalid mod name: ${f.nameWithoutExtension}")
            }
            val mod = f.nameWithoutExtension.substring(0, sepIndex)
            val version = f.nameWithoutExtension.substring(sepIndex + 1)
            println("Extra mod ${f.nameWithoutExtension} loaded as $mod:$version")
            modLocalRuntime("extra-mods:$mod:$version")
        }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
