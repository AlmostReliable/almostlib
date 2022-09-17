plugins {
    `maven-publish`
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

val minecraftVersion: String by project
val commonRunsEnabled: String by project
val commonClientRunName: String? by project
val commonServerRunName: String? by project
val modName: String by project
val modId: String by project
val fabricLoaderVersion: String by project
val mappingsChannel: String by project
val mappingsVersion: String by project
val kubejsVersion: String by project
val jeiVersion: String by project

val baseArchiveName = "${modName}-common-${minecraftVersion}"

base {
    archivesName.set(baseArchiveName)
}

minecraft {
    version(minecraftVersion)
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("dev.latvian.mods:kubejs:${kubejsVersion}")
}

tasks.processResources {
    val buildProps = project.properties

    filesMatching("pack.mcmeta") {
        expand(buildProps)
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = baseArchiveName
            from(components["java"])
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}

buildConfig {
    buildConfigField("String", "MOD_ID", "\"${modId}\"")
    buildConfigField("String", "MOD_VERSION", "\"${project.version}\"")
    buildConfigField("String", "MOD_NAME", "\"${modName}\"")

    packageName(project.group as String)
}
