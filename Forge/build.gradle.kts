plugins {
    java
    eclipse
    id("net.minecraftforge.gradle") version ("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
    id("org.spongepowered.mixin") version ("0.7-SNAPSHOT")
    `maven-publish`
}

val minecraftVersion: String by project
val forgeVersion: String by project
val modName: String by project
val modAuthor: String by project
val modId: String by project
val mappingsChannel: String by project
val mappingsVersion: String by project
val sharedRunDir: String by project

minecraft {
    mappings(mappingsChannel, "${mappingsVersion}-${minecraftVersion}")

    runs {
        create("client") {
            taskName("Client")
        }

        create("server") {
            taskName("Server")
        }

        forEach {
            it.workingDirectory(project.file(if (sharedRunDir.toBoolean()) "../run" else "run"))
            it.ideaModule("${rootProject.name}.${project.name}.main")
            it.property("mixin.env.remapRefMap", "true")
            it.property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            it.jvmArgs("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition")
            it.mods {
                create(modId) {
                    source(sourceSets.main.get())
                    source(project(":Common").sourceSets.main.get())
                }
            }
        }
    }
}

dependencies {
    /**
     * Core Mod Loader dependencies
     */
    minecraft("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    compileOnly(project(":Common"))
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    /**
     * Non Minecraft dependencies
     */
    compileOnly("com.google.auto.service:auto-service:1.0.1")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")

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
            implementation(fg.deobf("extra-mods:$mod:$version"))
        }
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId-common.mixins.json")
}

tasks {
    jar {
        finalizedBy("reobfJar")
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = base.archivesName.get()
            artifact(tasks.jar)
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
