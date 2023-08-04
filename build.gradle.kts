@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

val license: String by project
val minecraftVersion: String by project
val modVersion: String by project
val modId: String by project
val modName: String by project
val modDescription: String by project
val modAuthor: String by project
val autoServiceVersion: String by project
val manifoldVersion: String by project
val parchmentVersion: String by project
val fabricApiVersion: String by project
val forgeVersion: String by project
val githubRepo: String by project
val githubUser: String by project

plugins {
    id("architectury-plugin") version "3.4.+"
    id("dev.architectury.loom") version "1.3.+" apply false
    id("io.github.juuxel.loom-vineflower") version "1.11.0" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    java
    `maven-publish`
}

architectury {
    minecraft = minecraftVersion
}

/**
 * configurations for all projects including the root project
 */
allprojects {
    apply(plugin = "java")

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
            options.compilerArgs.add("-Xplugin:Manifold no-bootstrap")
        }

        withType<GenerateModuleMetadata> {
            enabled = false
        }
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        withSourcesJar()
    }
}

/**
 * configurations for all projects except the root project
 */
subprojects {
    apply(plugin = "architectury-plugin")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "io.github.juuxel.loom-vineflower")
    apply(plugin = "maven-publish")

    base {
        archivesName.set("$modId-${project.name.lowercase()}")
        version = "$minecraftVersion-$modVersion"
    }

    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots/") // Manifold
        maven("https://maven.parchmentmc.org") // Parchment
        mavenLocal()
    }

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
    loom.silentMojangMappingsLicense()

    dependencies {
        /**
         * Minecraft
         * Kotlin accessor methods are not generated in this gradle
         * they can be accessed through quoted names instead
         */
        "minecraft"("com.mojang:minecraft:$minecraftVersion")
        "mappings"(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
        })

        /**
         * non-Minecraft dependencies
         */
        compileOnly(testCompileOnly("com.google.auto.service:auto-service:$autoServiceVersion")!!)
        annotationProcessor(testAnnotationProcessor("com.google.auto.service:auto-service:$autoServiceVersion")!!)
        compileOnly(testCompileOnly("systems.manifold:manifold-ext-rt:$manifoldVersion")!!)
        annotationProcessor(testAnnotationProcessor("systems.manifold:manifold-ext:$manifoldVersion")!!)
    }

    tasks {
        /**
         * resource processing for defined targets
         * will replace `${key}` with the specified values from the map below
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
                // use major version for FML only because wrong Forge version error message
                // is way better than FML error message
                "forgeFMLVersion" to forgeVersion.substringBefore("."),
                "githubUser" to githubUser,
                "githubRepo" to githubRepo
            )

            println("[Process Resources] Replacing resource properties for project '${project.name}': ")
            replaceProperties.forEach { (key, value) -> println("\t -> $key = $value") }

            inputs.properties(replaceProperties)
            filesMatching(resourceTargets) {
                expand(replaceProperties)
            }
        }

        /**
         * exposing of Manifold extension methods to all projects depending on the lib
         */
        named<Jar>("jar") {
            manifest {
                attributes["Contains-Sources"] = "java,class"
            }
        }

        /**
         * when publishing to MavenLocal, use a timestamp as version so projects can always
         * use latest without generating a dummy version for cache busting
         */
        named<Task>("publishToMavenLocal") {
            version = "$version.${System.currentTimeMillis() / 1000}"
        }
    }

    /**
     * Maven publishing
     */
    publishing {
        publications {
            val mpm = project.properties["maven-publish-method"] as String
            println("[Publish Task] Publishing method for project '${project.name}': $mpm")
            register(mpm, MavenPublication::class) {
                artifactId = base.archivesName.get()
                from(components["java"])
            }
        }

        /**
         * information on how to set up publishing
         * https://docs.gradle.org/current/userguide/publishing_maven.html
         */
        repositories {
            // add repositories to publish here
        }
    }

    /**
     * disabling the runtime transformer from Architectury
     * if the runtime transformer should be enabled again, remove this block and
     * add the following to the respective subproject:
     *
     * configurations {
     *     "developmentFabric" { extendsFrom(configurations["common"]) }
     *     "developmentForge" { extendsFrom(configurations["common"]) }
     * }
     */
    architectury {
        compileOnly()
    }
}

/**
 * configurations for all subprojects except the common project
 */
subprojects {
    if (project.path == ":Common") {
        return@subprojects
    }

    apply(plugin = "com.github.johnrengelman.shadow")

    /**
     * add the outputs of the common test source set to the test source set classpath
     */
    sourceSets.named("test") {
        val cst = project(":Common").sourceSets.getByName("test")
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
                println("[Run Config] ${project.name} '${it.name}' directory: $dir")
                it.runDir(dir)
                // allows DCEVM hot-swapping when using the JBR (https://github.com/JetBrains/JetBrainsRuntime)
                it.vmArgs("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition")
            }
        }

        /**
         * "main" matches the default mod name
         * since `compileOnly()` is being used in Architectury, the local mods for the
         * loaders need to be set up too
         * otherwise, they won't recognize :Common.
         */
        with(mods.maybeCreate("main")) {
            fun Project.sourceSets() = extensions.getByName<SourceSetContainer>("sourceSets")
            sourceSet(sourceSets().getByName("main"))
            sourceSet(project(":Common").sourceSets().getByName("main"))
        }
    }

    val common by configurations.creating
    val shadowCommon by configurations.creating // don't use shadow from the plugin, IDEA shouldn't index this
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
            injectAccessWidener.set(true)
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
