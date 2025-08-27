rootProject.name = "QuickShop-OG"


ProcessBuilder("sh", "bootstrap.sh").directory(rootDir).inheritIO().start().let {
    if (it.waitFor() != 0) throw GradleException("bootstrap.sh failed")
}

file("libs")
    .listFiles()
    ?.filter { it.isDirectory && !it.name.startsWith(".") }
    ?.forEach { dir ->
        include(":libs:${dir.name}")
        project(":libs:${dir.name}").projectDir = dir
    }


include(
    "quickshop-common",
    "quickshop-api",

    "platform:quickshop-platform-interface",
    "platform:quickshop-platform-spigot-abstract",
    "platform:quickshop-platform-spigot-v1_19_R1",
    "platform:quickshop-platform-spigot-v1_19_R2",
    "platform:quickshop-platform-spigot-v1_19_R3",
    "platform:quickshop-platform-paper",

    "quickshop-bukkit",

    "compatibility:common",
    "compatibility:clearlag",
    "compatibility:nocheatplus",
    "compatibility:worldguard",
    "compatibility:worldedit",
    "compatibility:openinv",

    "addon:discount",
    "addon:list",
    "addon:shopitemonly",
    "addon:bluemap",
)
