package com.ghostchu.quickshop.command.subcommand;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.command.CommandHandler;
import com.ghostchu.quickshop.api.command.CommandParser;
import com.ghostchu.quickshop.api.shop.Shop;
import com.ghostchu.quickshop.util.MsgUtil;
import com.ghostchu.quickshop.util.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SubCommand_ListAll implements CommandHandler<CommandSender> {

    private static final int SHOPS_PER_PAGE = 10;
    private final QuickShop plugin;

    public SubCommand_ListAll(QuickShop plugin) {

        this.plugin = plugin;

    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull CommandParser parser) {

        int requestedPage = 1;
        if (!parser.getArgs().isEmpty()) {

            try {

                requestedPage = Integer.parseInt(parser.getArgs().get(0));

            } catch (NumberFormatException ignored) {

                sendUsage(sender, commandLabel);
                return;

            }

        }

        List<Shop> shops = new ArrayList<>(plugin.getShopManager().getAllShops());
        shops.sort(Comparator.comparingLong(Shop::getShopId));

        if (shops.isEmpty()) {

            MsgUtil.sendDirectMessage(sender, Component.text("No shops exist.", NamedTextColor.YELLOW));
            return;

        }

        int pageCount = Math.max(1, (shops.size() + SHOPS_PER_PAGE - 1) / SHOPS_PER_PAGE);
        if (requestedPage < 1 || requestedPage > pageCount) {

            sendUsage(sender, commandLabel);
            return;

        }

        int fromIndex = (requestedPage - 1) * SHOPS_PER_PAGE;
        int toIndex = Math.min(fromIndex + SHOPS_PER_PAGE, shops.size());

        MsgUtil.sendDirectMessage(sender,
                Component.text("All shops (page " + requestedPage + "/" + pageCount + ", " + shops.size() + " total)",
                        NamedTextColor.GOLD));

        for (Shop shop : shops.subList(fromIndex, toIndex)) {

            Location location = shop.getLocation();
            String worldName = location.getWorld() == null ? "unknown" : location.getWorld().getName();
            Component entry = Component.text("#" + shop.getShopId() + " ", NamedTextColor.DARK_GRAY)
                    .append(Util.getItemStackName(shop.getItem()).color(NamedTextColor.AQUA))
                    .append(Component.text(" | " + shop.getShopType().name().toLowerCase() + " | ",
                            NamedTextColor.GRAY))
                    .append(Component.text(shop.getPrice(), NamedTextColor.GREEN))
                    .append(Component.text(" | owner: ", NamedTextColor.GRAY)).append(shop.ownerName())
                    .append(Component.text(" | " + worldName + " " + location.getBlockX() + "," + location.getBlockY()
                            + "," + location.getBlockZ(), NamedTextColor.GRAY));
            MsgUtil.sendDirectMessage(sender, entry);

        }

        if (requestedPage < pageCount) {

            MsgUtil.sendDirectMessage(sender, Component
                    .text("Next page: /" + commandLabel + " listall " + (requestedPage + 1), NamedTextColor.YELLOW));

        }

    }

    private void sendUsage(@NotNull CommandSender sender, @NotNull String commandLabel) {

        MsgUtil.sendDirectMessage(sender,
                Component.text("Usage: /" + commandLabel + " listall [page]", NamedTextColor.RED));

    }

}
