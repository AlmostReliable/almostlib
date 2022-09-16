plugins {
    java
    eclipse
    `maven-publish`
    id("net.minecraftforge.gradle") version ("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
    id("org.spongepowered.mixin") version ("0.7.+")
}

val minecraftVersion: String by project
val mixinVersion: String by project
val forgeVersion: String by project
val modId: String by project
val mappingsChannel: String by project
val mappingsVersion: String by project
val extraModsDirectory: String by project
val jeiVersion: String by project

val baseArchiveName = "${modId}-forge-${minecraftVersion}"

base {
    archivesName.set(baseArchiveName)
}

minecraft {
    mappings(mappingsChannel, "${mappingsVersion}-${minecraftVersion}")

    runs {
        create("client") {
            workingDirectory(project.file("../run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Client")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            jvmArg("-XX:+AllowEnhancedClassRedefinition -XX:+IgnoreUnrecognizedVMOptions")
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                    source(project(":Common").sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("../run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Server")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            jvmArg("-XX:+AllowEnhancedClassRedefinition -XX:+IgnoreUnrecognizedVMOptions")
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                    source(project(":Common").sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

dependencies {
    minecraft("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")
    compileOnly(project(":Common"))

    compileOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-common-api:${jeiVersion}"))
    compileOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge-api:${jeiVersion}"))
    runtimeOnly(fg.deobf("mezz.jei:jei-${minecraftVersion}-forge:${jeiVersion}"))

    annotationProcessor("org.spongepowered:mixin:${mixinVersion}:processor")
}

mixin {
    add(sourceSets.main.get(), "${modId}.refmap.json")
    config("${modId}-common.mixins.json")
}

tasks.withType<JavaCompile> {
    source(project(":Common").sourceSets.main.get().allSource)
}

tasks.processResources {
    from(project(":Common").sourceSets.main.get().resources)
}

tasks {
    jar {
        finalizedBy("reobfJar")
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = baseArchiveName
            artifact(tasks.jar)
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
