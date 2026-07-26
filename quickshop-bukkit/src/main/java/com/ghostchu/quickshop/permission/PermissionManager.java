package com.ghostchu.quickshop.permission;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.obj.QUser;
import com.ghostchu.quickshop.api.permission.PermissionProvider;
import com.ghostchu.quickshop.api.permission.ProviderIsEmptyException;
import com.ghostchu.quickshop.util.Util;
import com.ghostchu.quickshop.util.logger.Log;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Getter
public class PermissionManager {

    private final QuickShop plugin;

    private final PermissionProvider provider;

    /**
     * The manager to call permission providers
     *
     * @param plugin Instance
     */
    public PermissionManager(QuickShop plugin) {

        this.plugin = plugin;
        provider = selectProvider(plugin);
        plugin.logger().info("Selected permission provider: {}", provider.getName());

    }

    @NotNull
    private static PermissionProvider selectProvider(QuickShop plugin) {

        try {

            return new LuckPermsPermissionProvider();

        } catch (ProviderIsEmptyException | NoClassDefFoundError e) {

            Log.debug("LuckPerms is not available, falling back to Bukkit superperms.");
            return new BukkitPermsProvider();

        }

    }

    /**
     * Check the permission for sender
     *
     * @param sender     The CommandSender you want check
     * @param permission The permission node wait to check
     * @return The result of check
     */
    public boolean hasPermission(@NotNull CommandSender sender, @NotNull String permission) {

        try {

            boolean result = provider.hasPermission(sender, permission);
            if (Util.isDevMode()) {

                Log.permission(sender.getName() + " : " + permission + " -> " + result);

            }

            return result;

        } catch (Exception th) {

            plugin.logger().warn(
                    "Failed to processing permission response, This might or not a bug, we not sure, but you can report to both permission provider plugin author or QuickShop devs about this error",
                    th);
            return false;

        }

    }

    /**
     * Check the permission for sender
     *
     * @param sender     The CommandSender you want check
     * @param permission The permission node wait to check
     * @return The result of check
     */
    public boolean hasPermission(@NotNull QUser sender, @NotNull String permission) {

        try {

            boolean result = provider.hasPermission(sender, permission);
            if (Util.isDevMode()) {

                Log.permission(sender.getDisplay() + " : " + permission + " -> " + result);

            }

            return result;

        } catch (Exception th) {

            plugin.logger().warn(
                    "Failed to processing permission response, This might or not a bug, we not sure, but you can report to both permission provider plugin author or QuickShop devs about this error",
                    th);
            return false;

        }

    }

}
