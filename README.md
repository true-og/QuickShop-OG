<!-- Links -->

[license]: https://github.com/Ghost-chu/QuickShop-Hikari/blob/hikari/LICENSE

[contributors]: https://github.com/Ghost-chu/QuickShop-Hikari/graphs/contributors

[fossaStatus]: https://app.fossa.com/projects/git%2Bgithub.com%2FGhost-chu%2FQuickShop-Hikari?ref=badge_shield

[fossaStatusLarge]: https://app.fossa.com/projects/git%2Bgithub.com%2FGhost-chu%2FQuickShop-Hikari?ref=badge_large

[quickshop-hikari]: https://github.com/Ghost-chu/QuickShop-Hikari

[quickshop-potato]: https://github.com/PotatoCraft-Studio/QuickShop-Reremake/

[quickshop-ghostchu]: https://github.com/Ghost-chu/QuickShop-Reremake

[quickshop-original]: https://github.com/KaiKikuchi/QuickShop

[clearlagg]: https://www.spigotmc.org/resources/68271/

[worldguard]: https://dev.bukkit.org/projects/worldguard

[nocheatplus]: https://www.spigotmc.org/resources/nocheatplus.26/

[openinv]: https://dev.bukkit.org/projects/openinv

[worldedit]: https://dev.bukkit.org/projects/worldedit

[optional_modules]: https://ci.codemc.io/job/Ghost-chu/job/QuickShop-Hikari/

[bStats-site]: https://bstats.org

[bStats-plugin]: https://bstats.org/plugin/bukkit/QuickShop-Hikari/14281

<!-- Images/Badges -->

[licenseBadge]: https://img.shields.io/github/license/Ghost-chu/QuickShop-Hikari.svg

[contributorsBadge]: https://img.shields.io/github/contributors/Ghost-chu/QuickShop-Hikari

[fossaStatusBadge]: https://app.fossa.com/api/projects/git%2Bgithub.com%2FGhost-chu%2FQuickShop-Hikari.svg?type=shield

[fossaStatusImageLarge]: https://app.fossa.com/api/projects/git%2Bgithub.com%2FGhost-chu%2FQuickShop-Hikari.svg?type=large

[JavaVersion]: https://img.shields.io/badge/Java-Versions_17_+_18-orange.svg

[MinecraftVersion]: https://img.shields.io/badge/Minecraft-Java%20Edition%201.18%2B-blueviolet

[bStatsImage]: https://bstats.org/signatures/bukkit/QuickShop-Hikari.svg

[Ver]: https://img.shields.io/spiget/version/100125?label=version

<!-- Start of README -->
# QuickShop-OG

[![licenseBadge]][license]
[![contributorsBadge]][contributors]
![passedTests]
[![fossaStatusBadge]][fossaStatus]

![JavaVersion]
![MinecraftVersion]
![Ver]

## Introduction

QuickShop-OG is a GPLv3 based fork of QuickShop Hikari maintained by [TrueOG Network](https://trueog.net) for Purpur 1.19.4. QuickShop Hikari is a Shop plugin that allows players to create Chest Shops to easily sell and buy items, without the
need for any commands.  
In fact, all commands in QuickShop are not even needed for normal gameplay.

QuickShop-OG is based on an older version of ([Ghost-chu/QuickShop-Hikari][quickshop-hikari]), which is a fork
from [PotatoCraft-Studio's version][quickshop-potato] which itself is a fork from the [Reremake][quickshop-ghostchu] of
the [original QuickShop][quickshop-original].

QuickShop Hikari is maintained by Ghost-Chu and has the goal to modernize the core content of QuickShop and adapt it to
the latest versions of Minecraft.

## Features

- Easy to use
- Toggleable Display Item on top of the chest.
- NBT Data, Enchantment, Tool Damage, Potion, and Mob Egg support.
- Unlimited chest support.
- Blacklist support & bypass permissions.
- Shops that buy and sell items at the same time (Using double chests).
- Customizable permission checks.
- UUID support.
- Better shop protection.
- i18n support for displayed Item names.
- i18n support for displayed Enchantment names.
- A cool item preview.
- World/region protection plugins support.
- ProtocolLib based Virtual Display Item support.
- Powerful API.
- Optimized performance.
- MiniMessage syntax support.
- H2 (local) or MySQL (remote) datasource support.
- Supports custom inventory! Use the InventoryWrapper API.
- Optimized for Paper, but also runs on Spigot (Though slower and with a more "hacky" approach).
- Advanced Transaction System. Undo any Inventory/Economy operation with a shop when it failed to prevent duplications
  and exploits.
- Per-shop permission management.
- Shop benefits between shop owner and other players!
- And much much more!

## Compatibility Modules

You can download optional modules [here][optional_modules] for compatibility with other plugins.

### [ClearLagg][clearlagg]

- Stops clearlagg from deleting the Display Item on any Shop.

### [NoCheatPlus][nocheatplus]

- Prevents NCP's anti-cheat checks from triggering when creating a shop.

### [OpenInv][openinv]

- Allow the usage of a Player's Ender Chest as Shop inventory by using `/quickshop echest`.

### [Worldedit][worldedit]

- Removes Shops that got deleted during a WorldEdit operation, to reduce "Ghost Shops".

### [Worldguard][worldguard]

- Flag-based shop control.

### Distributing forks

You're allowed to create your fork to share. No permission is needed.  

### Compile and Debugging

To compile and debug QuickShop, please do the following steps:

1. Make sure you're using Java 17 or 18. You can get the latest Java versions from the [Adoptium project][adoptium].
2. Compile the main project without a signature by using `mvn install -Pgithub` with the GitHub Profile selected.
3. Put the compiled jar into your Test-server's `plugins` folder, start the server and begin debugging!

To compile the QuickShop and debug it by yourself, please follow these steps:

1. Make sure you're using Java17+ JDK in your PATH.
2. Compile main-project without signature by using profile: `mvn install -Pgithub` with github profile selected.
3. Start your server and go on.

## bStats

QuickShop-Hikari collects certain statistic through [bStats][bstats-site].  
You may opt-out by setting `disabled-metrics` to `true` in the config.yml.

[![bStatsImage]][bStats-plugin]

## License

[![fossaStatusImageLarge]][fossaStatusLarge]

## Developer API

QuickShop-Hikari offers an API for you to use features such as retrieving active shops of a player.

### Maven

```xml

<repositories>
    <repository>
        <id>codemc</id>
        <url>https://repo.codemc.io/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
<!-- QuickShop Main Module -->
<dependency>
    <groupId>com.ghostchu</groupId>
    <artifactId>quickshop-bukkit</artifactId>
    <version>VERSION HERE</version>
    <scope>provided</scope>
    <classifier>shaded</classifier>
</dependency>
</dependencies>
```

### Gradle

```groovy
repositories {
    maven { url = "https://repo.codemc.io/repository/maven-public/" }
}

dependencies {
    compileOnly "com.ghostchu:quickshop-bukkit:VERSION HERE";
}
```

### Hook into the API

```java
public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        QuickShopAPI api = QuickShopAPI.getInstance();
        QuickShop instance = QuickShopAPI.getPluginInstance();
        QuickShop anotherWayToGetInstance = QuickShop.getInstance();
    }

}
```
