@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

val license: String by project
val fabricLoaderVersion: String by project
val fabricApiVersion: String by project
val forgeVersion: String by project
val minecraftVersion: String by project
val modPackage: String by project
val modVersion: String by project
val modId: String by project
val modName: String by project
val modDescription: String by project
val modAuthor: String by project
val githubRepo: String by project
val githubUser: String by project
val sharedRunDir: String by project
val autoServiceVersion: String by project
val parchmentVersion: String by project
val manifoldVersion: String by project

plugins {
    java
    `maven-publish`
    id("architectury-plugin") version ("3.4.+")
    id("io.github.juuxel.loom-quiltflower") version "1.10.0" apply false
    id("dev.architectury.loom") version ("1.2.+") apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

architectury {
    minecraft = minecraftVersion
}

val extraModsPrefix = "extra-mods"

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.parchmentmc.org") // Parchment
        maven("https://oss.sonatype.org/content/repositories/snapshots/") // Manifold
        flatDir {
            name = extraModsPrefix
            dir(file("$extraModsPrefix-$minecraftVersion"))
        }
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
            options.compilerArgs.add("-Xplugin:Manifold no-bootstrap")
        }
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        withSourcesJar()
    }
}

/**
 * Makes sure every dependency is also available inside test.
 */
configurations.named("testCompileOnly") {
    extendsFrom(configurations["compileOnly"])
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "maven-publish")
    apply(plugin = "io.github.juuxel.loom-quiltflower")

    base.archivesName.set("$modId-${project.name.lowercase()}")
    version = "$minecraftVersion-$modVersion"

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    /**
     * General dependencies used for all subprojects, e.g. mappings or the Minecraft version.
     */
    dependencies {
        /**
         * Kotlin accessor methods are not generated in this gradle, they can be accessed through quoted names.
         */
        "minecraft"("com.mojang:minecraft:$minecraftVersion")
        "mappings"(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
        })

        /**
         * Helps to load mods in development through an extra directory. Sadly this does not support transitive dependencies. :-(
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

        /**
         * Non-Minecraft dependencies
         */
        testCompileOnly(compileOnly("com.google.auto.service:auto-service:$autoServiceVersion")!!)
        testAnnotationProcessor(annotationProcessor("com.google.auto.service:auto-service:$autoServiceVersion")!!)
        compileOnly("systems.manifold:manifold-ext-rt:$manifoldVersion")
        testAnnotationProcessor(annotationProcessor("systems.manifold:manifold-ext:$manifoldVersion")!!)

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    }

    /**
     * Maven publishing
     */
    publishing {
        publications {
            val mpm = project.properties["maven-publish-method"] as String
            println("[Publish Task] Publishing method for project '${project.name}: $mpm")
            register(mpm, MavenPublication::class) {
                artifactId = base.archivesName.get()
                from(components["java"])
            }
        }

        // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
        repositories {
            // Add repositories to publish here.
        }
    }

    /**
     * Disabling the runtime transformer from Architectury here.
     * When the runtime transformer should be enabled again, remove this block and add the following to the respective subproject:
     *
     * configurations {
     *      "developmentFabric" { extendsFrom(configurations["common"]) } // or "developmentForge" for Forge
     * }
     */
    architectury {
        compileOnly()
    }

    tasks {
        /**
         * Resource processing for defined targets. This will replace `${key}` with the specified values from the map below.
         */
        processResources {
            val resourceTargets = listOf("META-INF/mods.toml", "pack.mcmeta", "fabric.mod.json")

            val replaceProperties = mapOf(
                "version" to project.version as String,
                "license" to license,
                "modId" to modId,
                "modName" to modName,
                "minecraftVersion" to minecraftVersion,
                "modAuthor" to modAuthor,
                "modDescription" to modDescription,
                "fabricApiVersion" to fabricApiVersion,
                "forgeVersion" to forgeVersion,
                "forgeFMLVersion" to forgeVersion.substringBefore("."), // Only use major version as FML error message sucks. The error message for wrong Forge version is way better.
                "githubUser" to githubUser,
                "githubRepo" to githubRepo
            )

            println("[Process Resources] Replacing properties in resources: ")
            replaceProperties.forEach { (key, value) -> println("\t -> $key = $value") }

            inputs.properties(replaceProperties)
            filesMatching(resourceTargets) {
                expand(replaceProperties)
            }
        }

        /**
         * Exposing of Manifold extension methods to all projects depending on the lib.
         */
        named<Jar>("jar") {
            manifest {
                attributes["Contains-Sources"] = "java,class"
            }
        }

        /**
         * When publishing to Maven Local, use a timestamp as version so projects can always
         * use the latest version without having to use a dummy version.
         */
        named<Task>("publishToMavenLocal") {
            version = "$version.${System.currentTimeMillis() / 1000}"
        }

        withType<Test> {
            useJUnitPlatform()
            defaultCharacterEncoding = "UTF-8"
        }
    }
}

/**
 * Subproject configurations and tasks only applied to subprojects that are not the common project, e.g. Fabric or Forge.
 */
subprojects {
    if (project.path == ":Common") {
        return@subprojects
    }

    apply(plugin = "com.github.johnrengelman.shadow")

    sourceSets.named("test") {
        val cst = project(":Common").sourceSets.getByName("test");
        this.compileClasspath += cst.output
        this.runtimeClasspath += cst.output
    }

    extensions.configure<LoomGradleExtensionAPI> {
        runs {
            named("client") {
                name("Testmod Client")
                source(sourceSets.test.get())
            }

            named("server") {
                name("Testmod Server")
                source(sourceSets.test.get())
            }

            create("gametest") {
                name("Gametest")
                server()
                source(sourceSets.test.get())
                property("fabric-api.gametest", "true")
                property("forge.gameTestServer", "true")
                property("almostlib.gametest.testPackages", "testmod.*")
            }

            forEach {
                val dir = "../run/${project.name.lowercase()}_${it.environment}"
                println("[${project.name}] Run config '${it.name}' directory set to: $dir")
                it.runDir(dir)
                // Allows DCEVM hot-swapping when using the JetBrains Runtime (https://github.com/JetBrains/JetBrainsRuntime).
                it.vmArgs("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition")
            }
        }


        /**
         * "main" matches the default mod's name. Since `compileOnly()` is being used in Architectury,
         * the local mods for the loaders need to be set up too. Otherwise, they won't recognize :Common.
         */
        with(mods.maybeCreate("main")) {
            fun Project.sourceSets() = extensions.getByName<SourceSetContainer>("sourceSets")
            sourceSet(sourceSets().getByName("main"))
            sourceSet(project(":Common").sourceSets().getByName("main"))
        }
    }

    val common by configurations.creating
    val shadowCommon by configurations.creating // Don't use shadow from the shadow plugin because IDEA isn't supposed to index this.
    configurations {
        "compileClasspath" { extendsFrom(common) }
        "runtimeClasspath" { extendsFrom(common) }
    }

    with(components["java"] as AdhocComponentWithVariants) {
        withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) { skip() }
    }

    tasks {
        named<ShadowJar>("shadowJar") {
            exclude("architectury.common.json")
            configurations = listOf(shadowCommon)
            archiveClassifier.set("dev-shadow")
        }

        named<RemapJarTask>("remapJar") {
            inputFile.set(named<ShadowJar>("shadowJar").get().archiveFile)
            dependsOn("shadowJar")
            archiveClassifier.set(null as String?)
        }

        named<Jar>("jar") {
            archiveClassifier.set("dev")
        }

        named<Jar>("sourcesJar") {
            val commonSources = project(":Common").tasks.named<Jar>("sourcesJar")
            dependsOn(commonSources)
            from(commonSources.get().archiveFile.map { zipTree(it) })
            archiveClassifier.set("sources")
        }
    }
}
