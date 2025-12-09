package com.ghostchu.quickshop.command.subcommand;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.command.CommandHandler;
import com.ghostchu.quickshop.api.command.CommandParser;
import com.ghostchu.quickshop.util.Util;
import com.ghostchu.quickshop.util.logger.Log;
import com.ghostchu.quickshop.util.paste.PasteGenerator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SubCommand_Paste implements CommandHandler<CommandSender> {

    private static final List<String> warningPluginList = List.of("ConsoleSpamFix", "ConsoleFilter", "LogFilter");
    private final QuickShop plugin;

    public SubCommand_Paste(QuickShop plugin) {

        this.plugin = plugin;

    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull CommandParser parser) {

        // do actions
        Util.asyncThreadRun(() -> {

            for (String s : warningPluginList) {

                if (Bukkit.getPluginManager().getPlugin(s) != null) {

                    if (parser.getArgs().stream().noneMatch(str -> str.contains("--force"))) {

                        plugin.text().of(sender, "paste-warning-plugin-find", s).send();
                        return;

                    }

                }

            }

            if (parser.getArgs().stream().anyMatch(str -> str.contains("file"))) {

                pasteToLocalFile(sender);
                return;

            }

            plugin.text().of(sender, "paste-upload-failed-local").send();
            pasteToLocalFile(sender);

        });

    }

    private boolean pasteToLocalFile(@NotNull CommandSender sender) {

        File file = new File(plugin.getDataFolder(), "paste");
        file.mkdirs();
        file = new File(file, "paste-" + UUID.randomUUID().toString().replace("-", "") + ".html");
        final String string = new PasteGenerator(sender).render();
        try {

            boolean createResult = file.createNewFile();
            Log.debug("Create paste file: " + file.getCanonicalPath() + " " + createResult);
            try (FileWriter fwriter = new FileWriter(file)) {

                fwriter.write(string);
                fwriter.flush();

            }

            plugin.text().of(sender, "paste-created-local", file.getAbsolutePath()).send();
            return true;

        } catch (IOException e) {

            plugin.logger().warn("Failed to save paste locally! The content will be send to the console", e);
            plugin.text().of("paste-created-local-failed").send();
            return false;

        }

    }

}
