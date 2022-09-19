@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

val minecraftVersion: String by project
val modName: String by project
val modId: String by project
val fabricLoaderVersion: String by project
val mappingsChannel: String by project
val mappingsVersion: String by project
val buildConfigPackage: String by project

loom {
    runConfigs.configureEach {
        ideConfigGenerated(false)
    }
}

dependencies {
    /**
     * Core Mod Loader dependencies
     */
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:$mappingsChannel-$minecraftVersion:$mappingsVersion@zip")
    })

    /**
     * Non Minecraft dependencies
     */
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    /**
     * DON'T USE THIS IN COMMON CODE! NEEDED TO COMPILE THIS PROJECT
     */
    modCompileOnly("net.fabricmc:fabric-loader:$fabricLoaderVersion")
}

tasks {
    withType<net.fabricmc.loom.task.AbstractRemapJarTask>().forEach { task -> task.targetNamespace.set("named") }
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

buildConfig {
    buildConfigField("String", "MOD_ID", "\"$modId\"")
    buildConfigField("String", "MOD_NAME", "\"$modName\"")
    buildConfigField("String", "MOD_VERSION", "\"$version\"")
    packageName(buildConfigPackage)
}
