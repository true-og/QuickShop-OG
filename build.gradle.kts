val quickshopVersion = "5.9"
val purpurApiVersion = "1.19.4-R0.1-SNAPSHOT"
val javaLanguageVersion = 17
val jetbrainsAnnotationsVersion = "24.1.0"
val adventureApiVersion = "4.22.0"
val adventureExtraVersion = "4.2.0"
val adventurePlatformVersion = "4.3.2"
val viaversionApi = "4.3.0"

plugins {
    id("java-library")
    id("io.freefair.lombok") version "8.14" apply false
    id("com.gradleup.shadow") version "8.3.6" apply false
    id("com.diffplug.spotless") version "7.0.4"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "eclipse")

    group = "com.ghostchu"
    version = quickshopVersion

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaLanguageVersion))
            vendor.set(org.gradle.jvm.toolchain.JvmVendorSpec.GRAAL_VM)
        }
    }

    repositories {
        mavenCentral()
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
    }

    dependencies { compileOnly("org.jetbrains:annotations:$jetbrainsAnnotationsVersion") }

    if (path != ":libs:Utilities-OG") {
        spotless {
            java {
                removeUnusedImports()
                palantirJavaFormat()
            }
            kotlinGradle {
                ktfmt().kotlinlangStyle().configure { it.setMaxWidth(120) }
                target("build.gradle.kts", "settings.gradle.kts")
            }
        }
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile>().configureEach {
        dependsOn("spotlessApply")
        options.compilerArgs.addAll(listOf("-parameters", "-Xlint:deprecation"))
        options.encoding = "UTF-8"
        options.isFork = true
    }
}

project(":quickshop-common") {
    dependencies {
        api("net.kyori:adventure-api:$adventureApiVersion")
        api("net.kyori:adventure-nbt:$adventureApiVersion")
        api("com.ghostchu:simplereloadlib:1.1.2")
        implementation("org.apache.commons:commons-lang3:3.14.0")
        implementation("org.slf4j:slf4j-jdk14:2.0.9")
        implementation("com.google.code.gson:gson:2.10.1")
        api("cc.carm.lib:easysql-hikaricp:0.4.7")
        api("org.apache.commons:commons-text:1.11.0")
        api("org.apache.commons:commons-compress:1.25.0")
        compileOnly("org.purpurmc.purpur:purpur-api:$purpurApiVersion")
        compileOnlyApi(project(":libs:Utilities-OG"))
    }
}

project(":quickshop-api") {
    dependencies {
        api(project(":quickshop-common"))
        implementation("com.vdurmont:semver4j:3.1.0")
        compileOnly("cc.carm.lib:easysql-api:0.4.7")
        compileOnly("org.purpurmc.purpur:purpur-api:$purpurApiVersion")
    }
}

val platformApi = "org.purpurmc.purpur:purpur-api:$purpurApiVersion"

project(":platform:quickshop-platform-interface") {
    dependencies {
        api(project(":quickshop-common"))
        compileOnly(platformApi)
        compileOnly("de.tr7zw:item-nbt-api-plugin:2.12.1")
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

project(":quickshop-bukkit") {
    tasks.processResources {
        val versionString = project.version.toString()
        val props = mapOf(
            "version" to versionString,
            "project" to mapOf("version" to versionString)
        )
        inputs.properties(props)
        filesMatching("plugin.yml") { expand(props) }
    }

    dependencies {
        implementation(project(":quickshop-api"))
        implementation(project(":platform:quickshop-platform-spigot-abstract"))
        implementation("com.ghostchu.crowdin:crowdinota:1.0.3")
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
        compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0")
        compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
        compileOnly("me.clip:placeholderapi:2.11.5")
        compileOnly("net.tnemc:EconomyCore:0.1.2.6-Pre1")
        compileOnly("net.tnemc:Reserve:0.1.5.3-SNAPSHOT-4")
        compileOnly("org.bstats:bstats-bukkit:3.0.2")
        compileOnly("com.konghq:unirest-java:3.14.5")
        compileOnly("com.github.juliomarcopineda:jdbc-stream:0.1.1")
        compileOnly("net.sourceforge.csvjdbc:csvjdbc:1.0.41")
        compileOnly("org.dom4j:dom4j:2.1.4")
        compileOnly("net.essentialsx:EssentialsX:2.20.1")
        compileOnly("com.rollbar:rollbar-java:1.10.0")
        compileOnly(files("lib/GemsEconomy-4.9.2.jar"))
    }

    tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        minimize()
        relocate("io.papermc.lib", "com.ghostchu.quickshop.shade.io.papermc.lib")
        relocate("de.tr7zw.changeme.nbtapi", "com.ghostchu.quickshop.shade.de.tr7zw.changeme.nbtapi")
        relocate("org.bstats", "com.ghostchu.quickshop.shade.org.bstats")
        relocate("de.themoep.minedown", "com.ghostchu.quickshop.shade.de.themoep.minedown")
        manifest { attributes["Main-Class"] = "com.ghostchu.quickshop.bootstrap.Bootstrap" }
        exclude(
            "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA",
            "META-INF/*.kotlin_module", "META-INF/*.txt",
            "META-INF/proguard/*", "META-INF/services/*",
            "META-INF/versions/9/*", "*License*", "*LICENSE*"
        )
    }

    tasks.named<Jar>("jar") { archiveClassifier.set("original") }

    tasks.register<Copy>("copyReleaseJar") {
        dependsOn("shadowJar")
        from(tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").flatMap { it.archiveFile })
        into(layout.projectDirectory.dir("../target"))
    }

    tasks.build {
        dependsOn(tasks.spotlessApply)
        dependsOn("shadowJar", "copyReleaseJar")
    }
}

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
        compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.8")
        compileOnly("com.github.juliomarcopineda:jdbc-stream:0.1.1")
        compileOnly("one.util:streamex:0.8.2")
    }
}
