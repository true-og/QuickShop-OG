rootProject.name = "QuickShop-OG"

// Run the bootstrap at configuration time.
val process = ProcessBuilder("sh", "bootstrap.sh").directory(rootDir).start()

val exitValue = process.waitFor()

if (exitValue != 0) {
    throw GradleException("bootstrap.sh failed with exit code $exitValue")
}

include(
    "libs:Utilities-OG",
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

