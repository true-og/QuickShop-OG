/* This is free and unencumbered software released into the public domain */

import org.gradle.kotlin.dsl.provideDelegate

extra["kotlinAttribute"] = Attribute.of("kotlin-tag", Boolean::class.javaObjectType)

val kotlinAttribute: Attribute<Boolean> by rootProject.extra

val quickshopVersion = "5.9"
val purpurApiVersion = "1.19.4-R0.1-SNAPSHOT"
val javaLanguageVersion = 17
val jetbrainsAnnotationsVersion = "24.1.0"
val adventureApiVersion = "4.22.0"
val adventureExtraVersion = "4.2.0"
val adventurePlatformVersion = "4.3.2"
val viaversionApi = "4.3.0"

/* ------------------------------ Plugins ------------------------------ */
plugins {
    id("java") // Import Java plugin.
    id("java-library") // Import Java Library plugin.
    id("com.diffplug.spotless") version "8.1.0" // Import Spotless plugin.
    id("com.gradleup.shadow") version "8.3.9" apply false // Import Shadow plugin.
    id("checkstyle") // Import Checkstyle plugin.
    id("io.freefair.lombok") version "8.13.1" apply false // Import automatic lombok support.
    eclipse // Import Eclipse plugin.
    kotlin("jvm") version "2.1.21" // Import Kotlin JVM plugin.
}

/* --------------------------- JDK / Kotlin ---------------------------- */
subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "eclipse")

    group = "com.ghostchu"
    version = quickshopVersion

    java {
        sourceCompatibility = JavaVersion.VERSION_17 // Compile with JDK 17 compatibility.
        toolchain { // Select Java toolchain.
            languageVersion.set(JavaLanguageVersion.of(javaLanguageVersion)) // Use JDK 17.
            vendor.set(org.gradle.jvm.toolchain.JvmVendorSpec.GRAAL_VM) // Use GraalVM CE.
        }
    }

    /* ---------------------------- Repos ---------------------------------- */
    repositories {
        mavenCentral() // Import the Maven Central Maven Repository.
        maven("https://repo.viaversion.com")
        maven("https://repo.purpurmc.org/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://repo.md-5.net/content/repositories/snapshots/")
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://repo.essentialsx.net/releases/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://m2.dv8tion.net/releases")
        maven("https://nexus.scarsz.me/content/groups/public/")
        maven("https://www.jitpack.io")
        maven { url = uri("file://${System.getProperty("user.home")}/.m2/repository") }
        System.getProperty("SELF_MAVEN_LOCAL_REPO")?.let { // TrueOG Bootstrap mavenLocal().
            val dir = file(it)
            if (dir.isDirectory) {
                println("Using SELF_MAVEN_LOCAL_REPO at: $it")
                maven { url = uri("file://${dir.absolutePath}") }
            } else {
                mavenLocal()
            }
        }
    }

    /* ---------------------- Java project deps ---------------------------- */
    dependencies {
        compileOnly("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    }

    /* ----------------------------- Auto Formatting ------------------------ */
    if (path != ":libs:Utilities-OG") {
        spotless {
            java {
                eclipse().configFile(rootProject.file("config/formatter/eclipse-java-formatter.xml")) // Eclipse java formatting.
                leadingTabsToSpaces() // Convert leftover leading tabs to spaces.
                removeUnusedImports() // Remove imports that aren't being called.
            }
            kotlinGradle {
                ktfmt().kotlinlangStyle().configure { it.setMaxWidth(120) } // JetBrains Kotlin formatting.
                target("build.gradle.kts", "settings.gradle.kts") // Gradle files to format.
            }
        }
    }

    /* ---------------------- Reproducible jars ---------------------------- */
    tasks.withType<AbstractArchiveTask>().configureEach { // Ensure reproducible .jars
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    /* --------------------------- Javac opts ------------------------------- */
    tasks.withType<JavaCompile>().configureEach {
        dependsOn("spotlessApply") // Run spotless before compiling with the JDK.
        options.compilerArgs.add("-parameters") // Enable reflection for java code.
        options.isFork = true // Run javac in its own process.
        options.compilerArgs.add("-Xlint:deprecation") // Trigger deprecation warning messages.
        options.encoding = "UTF-8" // Use UTF-8 file encoding.
    }
}

/* ---------------------- Module: quickshop-common ---------------------- */
project(":quickshop-common") {
    dependencies {
        compileOnlyApi("net.kyori:adventure-api:$adventureApiVersion")
        compileOnlyApi("net.kyori:adventure-nbt:$adventureApiVersion")
        compileOnlyApi("com.ghostchu:simplereloadlib:1.1.2")
        compileOnlyApi("org.apache.commons:commons-lang3:3.14.0")
        compileOnlyApi("org.slf4j:slf4j-jdk14:2.0.9")
        compileOnlyApi("com.google.code.gson:gson:2.10.1")
        compileOnlyApi("cc.carm.lib:easysql-hikaricp:0.4.7")
        compileOnlyApi("org.apache.commons:commons-text:1.11.0")
        compileOnlyApi("org.apache.commons:commons-compress:1.25.0")
        compileOnly("org.purpurmc.purpur:purpur-api:$purpurApiVersion")
        compileOnlyApi(project(":libs:Utilities-OG"))
    }
}

/* ------------------------ Module: quickshop-api ----------------------- */
project(":quickshop-api") {
    dependencies {
        api(project(":quickshop-common"))
        compileOnlyApi("com.vdurmont:semver4j:3.1.0")
        compileOnly("cc.carm.lib:easysql-api:0.4.7")
        compileOnly("org.purpurmc.purpur:purpur-api:$purpurApiVersion")
    }
}

val platformApi = "org.purpurmc.purpur:purpur-api:$purpurApiVersion"

/* --------------- Module: platform-interface & friends ---------------- */
project(":platform:quickshop-platform-interface") {
    dependencies {
        api(project(":quickshop-common"))
        compileOnly(platformApi)
        compileOnly("de.tr7zw:item-nbt-api-plugin:2.14.1")
    }
}

project(":platform:quickshop-platform-spigot-abstract") {
    dependencies {
        api(project(":platform:quickshop-platform-interface"))
        compileOnly(platformApi)
        implementation("commons-lang:commons-lang:2.6")
        compileOnly("de.tr7zw:item-nbt-api-plugin:2.12.1")
    }
}

project(":platform:quickshop-platform-paper") {
    dependencies {
        implementation(project(":platform:quickshop-platform-interface"))
        compileOnly("io.papermc.paper:paper-api:$purpurApiVersion")
    }
}

listOf(
    "v1_19_R1" to "1.19.1-R0.1-SNAPSHOT",
    "v1_19_R2" to "1.19.3-R0.1-SNAPSHOT",
    "v1_19_R3" to "1.19.4-R0.1-SNAPSHOT"
).forEach { (suffix, spigotVer) ->
    project(":platform:quickshop-platform-spigot-$suffix") {
        dependencies {
            api(project(":platform:quickshop-platform-spigot-abstract"))
            compileOnly("org.spigotmc:spigot:$spigotVer:remapped-mojang")
            compileOnly("de.tr7zw:item-nbt-api-plugin:2.12.1")
        }
    }
}

/* ---------------------- Module: quickshop-bukkit ---------------------- */
project(":quickshop-bukkit") {
    /* ----------------------------- Resources ----------------------------- */
    tasks.processResources {
        val versionString = project.version.toString()
        val props = mapOf(
            "version" to versionString,
            "project" to mapOf("version" to versionString)
        )
        inputs.properties(props)
        filesMatching("plugin.yml") { expand(props) }
    }

    /* ---------------------- Java project deps ---------------------------- */
    val protocolLibJar = rootProject.file("libs/ProtocolLib/ProtocolLib-5.0.jar")

    dependencies {
        implementation(project(":quickshop-api"))
        implementation(project(":platform:quickshop-platform-spigot-abstract"))
        compileOnly("com.ghostchu.crowdin:crowdinota:1.0.3")
        implementation("io.papermc:paperlib:1.0.7")
        compileOnly(project(":libs:Utilities-OG"))
        listOf(
            ":platform:quickshop-platform-spigot-v1_19_R1",
            ":platform:quickshop-platform-spigot-v1_19_R2",
            ":platform:quickshop-platform-spigot-v1_19_R3",
            ":platform:quickshop-platform-paper"
        ).forEach { implementation(project(it)) }
        compileOnly(platformApi)
        compileOnly("net.kyori:adventure-platform-bukkit:$adventurePlatformVersion")
        compileOnly("net.kyori:adventure-platform-viaversion:$adventurePlatformVersion")
        compileOnly("net.kyori:adventure-text-serializer-ansi:$adventureExtraVersion")
        compileOnly("net.kyori:adventure-text-serializer-bungeecord:$adventureExtraVersion")
        compileOnly("com.viaversion:viaversion-api:$viaversionApi")
        compileOnly("com.h2database:h2:2.2.224")
        compileOnly(files(protocolLibJar)) // Import Legacy ProtocolLib API.
        compileOnly("me.xanium:GemsEconomy:4.9.3-GCRemake-1.6")
        compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
        compileOnly("me.clip:placeholderapi:2.11.5")
        compileOnly("net.tnemc:EconomyCore:0.1.2.6-Pre1")
        compileOnly("net.tnemc:Reserve:0.1.5.3-SNAPSHOT-4")
        compileOnly("com.konghq:unirest-java:3.14.5")
        compileOnly("com.github.juliomarcopineda:jdbc-stream:0.1.1")
        compileOnly("net.sourceforge.csvjdbc:csvjdbc:1.0.41")
        compileOnly("org.dom4j:dom4j:2.1.4")
        compileOnly("net.essentialsx:EssentialsX:2.20.1")
        compileOnly("com.rollbar:rollbar-java:1.10.0")
    }

    /* ------------------------------ Eclipse ------------------------------ */
    val eclipseCompileOnly = configurations.register("eclipseCompileOnly") {
        isCanBeResolved = true
        extendsFrom(configurations.compileOnly.get())
    }.get()

    eclipse.classpath.plusConfigurations.add(eclipseCompileOnly)

    /* ----------------------------- Shadow -------------------------------- */
    tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("") // Use empty string instead of null.
        minimize()
        relocate("io.papermc.lib", "com.ghostchu.quickshop.shade.io.papermc.lib")
        relocate("de.tr7zw.changeme.nbtapi", "com.ghostchu.quickshop.shade.de.tr7zw.changeme.nbtapi")
        relocate("de.themoep.minedown", "com.ghostchu.quickshop.shade.de.themoep.minedown")
        manifest { attributes["Main-Class"] = "com.ghostchu.quickshop.bootstrap.Bootstrap" }
        exclude(
            "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA",
            "META-INF/*.kotlin_module", "META-INF/*.txt",
            "META-INF/proguard/*", "META-INF/services/*",
            "META-INF/versions/9/*", "*License*", "*LICENSE*"
        )
    }

    tasks.named<Jar>("jar") { archiveClassifier.set("original") } // Applies to root jarfile only.

    tasks.register<Copy>("copyReleaseJar") {
        dependsOn("shadowJar")
        from(tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs"))
        rename { "QuickShop-OG.jar" }
    }

    tasks.build {
        dependsOn(tasks.spotlessApply, tasks.named("shadowJar"), tasks.named("copyReleaseJar")) // Build depends on spotless and shadow.
    }
}

/* ------------------------- Add-on modules ---------------------------- */
val commonAddOnDeps = listOf(
    platformApi,
    "org.apache.commons:commons-lang3:3.14.0"
)

project(":addon:bluemap") {
    dependencies {
        compileOnly(project(":quickshop-bukkit"))
        compileOnly(project(":quickshop-api"))
        compileOnly("com.github.BlueMap-Minecraft:BlueMapAPI:2.6.2")
        commonAddOnDeps.forEach { compileOnly(it) }
    }
}

listOf("discount", "list", "shopitemonly").forEach { name ->
    project(":addon:$name") {
        dependencies {
            compileOnly(project(":quickshop-bukkit"))
            compileOnly(project(":quickshop-api"))
            commonAddOnDeps.forEach { compileOnly(it) }
        }
    }
}

/* ------------------------- Compatibility mods ------------------------ */
project(":compatibility:common") {
    dependencies {
        compileOnly(platformApi)
        api(project(":quickshop-common"))
        compileOnly(project(":quickshop-api"))
        compileOnly(project(":quickshop-bukkit"))
    }
}

project(":compatibility:clearlag") {
    dependencies {
        implementation(project(":compatibility:common"))
        compileOnly(project(":quickshop-bukkit"))
        compileOnly(platformApi)
        compileOnly(files("lib/Clearlag-3.2.2.jar"))
    }
}

project(":compatibility:nocheatplus") {
    dependencies {
        implementation(project(":compatibility:common"))
        compileOnly(project(":quickshop-bukkit"))
        compileOnly(platformApi)
        compileOnly(project(":quickshop-api"))
        compileOnly("fr.neatmonster:nocheatplus:3.16.1-SNAPSHOT")
    }
}

project(":compatibility:openinv") {
    dependencies {
        implementation(project(":compatibility:common"))
        compileOnly(project(":quickshop-bukkit"))
        compileOnly(platformApi)
        compileOnly(project(":quickshop-api"))
        compileOnly("com.github.true-og.OpenInv:openinvapi:a85e8ebc28")
    }
}

project(":compatibility:worldedit") {
    dependencies {
        implementation(project(":compatibility:common"))
        compileOnly(project(":quickshop-bukkit"))
        compileOnly(project(":quickshop-api"))
        compileOnly(platformApi)
        compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.17")
    }
}

project(":compatibility:worldguard") {
    dependencies {
        implementation(project(":compatibility:common"))
        compileOnly(project(":quickshop-bukkit"))
        compileOnly(project(":quickshop-api"))
        compileOnly(platformApi)
        compileOnly("com.github.juliomarcopineda:jdbc-stream:0.1.1")
        compileOnly("one.util:streamex:0.8.2")
        compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.8")
    }
}

apply(from = "eclipse.gradle.kts") // Import eclipse classpath support script.

/* ------------------------------ Eclipse SHIM ------------------------- */

// This can't be put in eclipse.gradle.kts because Gradle is weird.
subprojects {
    apply(plugin = "java-library")
    apply(plugin = "eclipse")
    eclipse.project.name = "${project.name}-${rootProject.name}"
    tasks.withType<Jar>().configureEach { archiveBaseName.set("${project.name}-${rootProject.name}") }
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.named("build") {
    dependsOn(":quickshop-bukkit:copyReleaseJar")
}
