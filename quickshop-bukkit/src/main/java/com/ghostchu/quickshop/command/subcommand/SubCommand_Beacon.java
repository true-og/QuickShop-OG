package com.ghostchu.quickshop.command.subcommand;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.command.CommandHandler;
import com.ghostchu.quickshop.api.command.CommandParser;
import com.ghostchu.quickshop.api.shop.Shop;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

// Marks empty shops with a particle column sent only to the command runner.
// The sender-only marker idea comes from WorldEditSUI, no code is copied.
// WorldEditSUI and QuickShop-OG are both GPL v3, so both stay compatible.
public class SubCommand_Beacon implements CommandHandler<Player> {

    private static final int DEFAULT_RADIUS = 128;

    private static final int MAX_RADIUS = 512;

    private static final int MAX_BEACONS = 64;

    private static final int VERTICAL_STEP = 2;

    private static final long REFRESH_TICKS = 10L;

    private static final long DURATION_TICKS = 20L * 60L;

    private final QuickShop plugin;

    private final Map<UUID, BukkitTask> activeBeacons = new HashMap<>();

    public SubCommand_Beacon(QuickShop plugin) {

        this.plugin = plugin;

    }

    @Override
    public void onCommand(@NotNull Player sender, @NotNull String commandLabel, @NotNull CommandParser parser) {

        BukkitTask running = activeBeacons.remove(sender.getUniqueId());
        if (running != null) {

            running.cancel();
            sender.sendMessage("[QuickShop] Empty shop beacons hidden.");
            return;

        }

        int radius = DEFAULT_RADIUS;
        List<String> arguments = parser.getArgs();
        if (!arguments.isEmpty()) {

            try {

                radius = Integer.parseInt(arguments.get(0));

            } catch (NumberFormatException e) {

                sender.sendMessage("[QuickShop] " + arguments.get(0) + " is not a valid radius.");
                return;

            }

            if (radius < 1 || radius > MAX_RADIUS) {

                sender.sendMessage("[QuickShop] The radius must be between 1 and " + MAX_RADIUS + ".");
                return;

            }

        }

        List<Location> targets = findEmptyShops(sender, radius);
        if (targets.isEmpty()) {

            sender.sendMessage("[QuickShop] No empty shops found within " + radius + " blocks.");
            return;

        }

        boolean truncated = targets.size() > MAX_BEACONS;
        if (truncated) {

            targets = new ArrayList<>(targets.subList(0, MAX_BEACONS));

        }

        startBeacons(sender, targets);
        sender.sendMessage("[QuickShop] Showing " + targets.size() + " empty shop beacon(s) for "
                + (DURATION_TICKS / 20L) + " seconds. Run the command again to hide them.");
        if (truncated) {

            sender.sendMessage("[QuickShop] Only the closest " + MAX_BEACONS + " empty shops are shown.");

        }

    }

    // Empty means the shop cannot serve its side of the trade.
    private boolean isEmpty(@NotNull Shop shop) {

        if (shop.isUnlimited()) {

            return false;

        }

        if (shop.isSelling()) {

            return shop.getRemainingStock() == 0;

        }

        return shop.getRemainingSpace() == 0;

    }

    private List<Location> findEmptyShops(@NotNull Player sender, int radius) {

        World world = sender.getWorld();
        Location origin = sender.getLocation();
        long squaredRadius = (long) radius * radius;
        List<Location> found = new ArrayList<>();
        for (Shop shop : plugin.getShopManager().getShopsInWorld(world)) {

            Location location = shop.getLocation();
            if (location.getWorld() == null || !location.getWorld().equals(world)) {

                continue;

            }

            double deltaX = location.getX() - origin.getX();
            double deltaZ = location.getZ() - origin.getZ();
            if (deltaX * deltaX + deltaZ * deltaZ > squaredRadius) {

                continue;

            }

            if (!isEmpty(shop)) {

                continue;

            }

            found.add(location.clone());

        }

        // Closest first, so truncation keeps the nearest shops.
        found.sort((left, right) -> Double.compare(left.distanceSquared(origin), right.distanceSquared(origin)));
        return found;

    }

    private void startBeacons(@NotNull Player sender, @NotNull List<Location> targets) {

        List<Location> immutableTargets = Collections.unmodifiableList(targets);
        UUID senderId = sender.getUniqueId();
        BukkitTask task = new BukkitRunnable() {

            private long elapsed = 0L;

            @Override
            public void run() {

                Player online = plugin.getJavaPlugin().getServer().getPlayer(senderId);
                if (online == null || !online.isOnline() || elapsed >= DURATION_TICKS) {

                    activeBeacons.remove(senderId);
                    cancel();
                    return;

                }

                for (Location target : immutableTargets) {

                    if (target.getWorld() == null || !target.getWorld().equals(online.getWorld())) {

                        continue;

                    }

                    drawColumn(online, target);

                }

                elapsed += REFRESH_TICKS;

            }

        }.runTaskTimer(plugin.getJavaPlugin(), 0L, REFRESH_TICKS);
        activeBeacons.put(senderId, task);

    }

    private void drawColumn(@NotNull Player viewer, @NotNull Location target) {

        World world = target.getWorld();
        if (world == null) {

            return;

        }

        double x = target.getBlockX() + 0.5D;
        double z = target.getBlockZ() + 0.5D;
        int top = world.getMaxHeight();
        for (int y = target.getBlockY(); y < top; y += VERTICAL_STEP) {

            // A Player receiver sends the packet to that player only.
            viewer.spawnParticle(Particle.END_ROD, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);

        }

    }

    // Cancels every running beacon task, used when the plugin unloads.
    public void cancelAll() {

        for (BukkitTask task : activeBeacons.values()) {

            task.cancel();

        }

        activeBeacons.clear();

    }

}
