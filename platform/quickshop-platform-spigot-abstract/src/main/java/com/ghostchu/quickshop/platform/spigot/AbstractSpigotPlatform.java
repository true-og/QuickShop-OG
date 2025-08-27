package com.ghostchu.quickshop.platform.spigot;

import com.ghostchu.quickshop.common.util.QuickSLF4JLogger;
import com.ghostchu.quickshop.platform.Platform;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.trueog.utilitiesog.UtilitiesOG;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSpigotPlatform implements Platform {

    protected final Logger logger = Logger.getLogger("QuickShop-Hikari");
    protected Map<String, String> translationMapping;

    public AbstractSpigotPlatform(@NotNull Plugin instance) {

        if (Bukkit.getPluginManager().getPlugin("NBTAPI") == null) {

            throw new IllegalStateException("Must install NBT-API if you're running on Spigot server");

        }

    }

    @NotNull
    public static String getNMSVersion() {

        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);

    }

    @Override
    public @NotNull Component getDisplayName(@NotNull ItemStack stack) {

        if (stack.getItemMeta() != null && stack.getItemMeta().hasDisplayName()) {

            return stack.getItemMeta().displayName();

        }

        return Component.empty();

    }

    @Override
    public @NotNull Component getDisplayName(@NotNull ItemMeta meta) {

        if (meta.hasDisplayName()) {

            return meta.displayName();

        }

        return Component.empty();

    }

    @Override
    public @NotNull Component getLine(@NotNull Sign sign, int line) {

        return UtilitiesOG.trueogColorize(sign.getLine(line));

    }

    @Override
    public @Nullable List<Component> getLore(@NotNull ItemStack stack) {

        if (!stack.hasItemMeta())
            return null;
        ItemMeta meta = stack.getItemMeta();
        if (!meta.hasLore())
            return null;
        return meta.getLore().stream().map(UtilitiesOG::trueogColorize).collect(Collectors.toList());

    }

    @Override
    public @Nullable List<Component> getLore(@NotNull ItemMeta meta) {

        if (!meta.hasLore())
            return null;
        return meta.getLore().stream().map(UtilitiesOG::trueogColorize).collect(Collectors.toList());

    }

    @Override
    public @NotNull Component getTranslation(@NotNull Material material) {

        return Component.translatable(getTranslationKey(material));

    }

    @Override
    public @NotNull Component getTranslation(@NotNull EntityType entity) {

        return Component.translatable(getTranslationKey(entity));

    }

    @Override
    public @NotNull Component getTranslation(@NotNull PotionEffectType potionEffectType) {

        return Component.translatable(getTranslationKey(potionEffectType));

    }

    @Override
    public @NotNull Component getTranslation(@NotNull Enchantment enchantment) {

        return Component.translatable(getTranslationKey(enchantment));

    }

    @Override
    public @NotNull Component getTranslation(@NotNull ItemStack itemStack) {

        return Component.translatable(getTranslationKey(itemStack));

    }

    @Override
    public MiniMessage miniMessage() {

        return MiniMessage.miniMessage();

    }

    @Override
    public void sendMessage(@NotNull CommandSender sender, @NotNull Component component) {

        sender.sendMessage(MiniMessage.miniMessage().serialize(component));

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void sendSignTextChange(@NotNull Player player, @NotNull Sign sign, boolean glowing,
            @NotNull List<Component> components)
    {

    }

    @Override
    public void setDisplayName(@NotNull ItemStack stack, @Nullable Component component) {

        if (!stack.hasItemMeta())
            return;
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(component);
        stack.setItemMeta(meta);

    }

    @Override
    public void setDisplayName(@NotNull Item stack, @Nullable Component component) {

        stack.setCustomName(component == null ? null : MiniMessage.miniMessage().serialize(component));

    }

    @Override
    public void setLines(@NotNull Sign sign, @NotNull List<Component> component) {

        String emptyJson = "{\"text\":\"\"}";
        ReadWriteNBT root = NBT.createNBTObject();
        ReadWriteNBT frontText = root.getOrCreateCompound("front_text");
        ReadWriteNBTList<String> messages = frontText.getStringList("messages");
        for (int i = 0; i < 4; i++) {

            Component com = component.get(i);
            String json = com == null ? emptyJson : "{\"text\":\"" + MiniMessage.miniMessage().serialize(com) + "\"}";
            root.setString("Text" + (i + 1), json);
            messages.add(json);

        }

        NBT.modify(sign, (Consumer<ReadWriteNBT>) nbt -> nbt.mergeCompound(root));

    }

    @Override
    public void setLore(@NotNull ItemStack stack, @NotNull Collection<Component> components) {

        if (!stack.hasItemMeta())
            return;
        ItemMeta meta = stack.getItemMeta();
        meta.lore(components.stream().collect(Collectors.toList()));
        stack.setItemMeta(meta);

    }

    @Override
    public void updateTranslationMappingSection(@NotNull Map<String, String> mapping) {

        this.translationMapping = mapping;

    }

    @Override
    @NotNull
    public org.slf4j.Logger getSlf4jLogger(@NotNull Plugin parent) {

        return QuickSLF4JLogger.initializeLoggerService(parent.getLogger());

    }

    private String postProcessingTranslationKey(String key) {

        return this.translationMapping.getOrDefault(key, key);

    }

}
