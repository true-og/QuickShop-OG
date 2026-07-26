package com.ghostchu.quickshop.permission;

import com.ghostchu.quickshop.api.obj.QUser;
import com.ghostchu.quickshop.api.permission.PermissionProvider;
import com.ghostchu.quickshop.api.permission.ProviderIsEmptyException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Queries LuckPerms directly, replacing the Vault permission bridge.
 * <p>
 * Checks go through PlayerAdapter, which resolves against the player's current
 * contexts. This matches Player#hasPermission as long as LuckPerms keeps
 * apply-bukkit-default-permissions, apply-bukkit-child-permissions and
 * apply-bukkit-attachment-permissions enabled (all default to true) -
 * QuickShop's plugin.yml relies on both defaults and children trees.
 */
public class LuckPermsPermissionProvider implements PermissionProvider {

    private final LuckPerms api;

    private final PlayerAdapter<Player> playerAdapter;

    public LuckPermsPermissionProvider() {

        try {

            this.api = LuckPermsProvider.get();

        } catch (IllegalStateException | NoClassDefFoundError e) {

            throw new ProviderIsEmptyException(getName());

        }

        this.playerAdapter = this.api.getPlayerAdapter(Player.class);

    }

    /**
     * Get the debug infos in provider
     *
     * @param sender     CommandSender
     * @param permission The permission want to check
     * @return Debug Infos
     */
    public @NotNull PermissionInformationContainer getDebugInfo(@NotNull CommandSender sender,
            @NotNull String permission)
    {

        if (sender instanceof Server) {

            return new PermissionInformationContainer(sender, permission, null, "User is Console");

        }

        if (!(sender instanceof Player player)) {

            return new PermissionInformationContainer(sender, permission, null, "Sender is not a LuckPerms user");

        }

        User user = playerAdapter.getUser(player);
        return new PermissionInformationContainer(sender, permission, user.getPrimaryGroup(), null);

    }

    @Override
    public @NotNull String getName() {

        return "LuckPerms";

    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender, @NotNull String permission) {

        if (sender instanceof Player player) {

            return playerAdapter.getPermissionData(player).checkPermission(permission).asBoolean();

        }

        // Console and command blocks have no LuckPerms user to resolve against.
        return sender.hasPermission(permission);

    }

    @Override
    public boolean hasPermission(@NotNull QUser sender, @NotNull String permission) {

        Player player = sender.getBukkitPlayer().orElse(null);
        if (player == null) {

            return false;

        }

        return playerAdapter.getPermissionData(player).checkPermission(permission).asBoolean();

    }

}
