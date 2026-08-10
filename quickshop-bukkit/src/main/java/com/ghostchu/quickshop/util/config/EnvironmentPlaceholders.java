package com.ghostchu.quickshop.util.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

// Keeps SubstAgent placeholders on disk when the config is written back.
// SubstAgent substitutes while the JVM reads through FileInputStream.
// Bukkit loads configs that way, so the in-memory value is already resolved.
// NIO reads are not intercepted, so the raw tokens are recovered with NIO.
public final class EnvironmentPlaceholders {

    // SubstAgent $ENV_VAR form, plus the older ^{ENV_VAR} form.
    private static final Pattern PLACEHOLDER = Pattern
            .compile("\\$[A-Za-z_][A-Za-z0-9_]*|\\^\\{[A-Za-z_][A-Za-z0-9_]*}");

    private EnvironmentPlaceholders() {

    }

    // Reads the placeholder tokens still present in the file on disk.
    @NotNull
    public static Map<String, String> readTokens(@NotNull File file) {

        if (!file.isFile()) {

            return Collections.emptyMap();

        }

        String raw;
        try {

            // NIO read, a FileInputStream read would be substituted.
            raw = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

        } catch (IOException e) {

            return Collections.emptyMap();

        }

        YamlConfiguration onDisk = new YamlConfiguration();
        try {

            onDisk.loadFromString(raw);

        } catch (InvalidConfigurationException e) {

            return Collections.emptyMap();

        }

        Map<String, String> tokens = new LinkedHashMap<>();
        for (String key : onDisk.getKeys(true)) {

            Object value = onDisk.get(key);
            if (value instanceof String string && PLACEHOLDER.matcher(string).find()) {

                tokens.put(key, string);

            }

        }

        return tokens;

    }

    // Saves the config without persisting substituted placeholder values.
    public static void saveConfig(@NotNull JavaPlugin plugin) {

        File file = new File(plugin.getDataFolder(), "config.yml");
        Map<String, String> tokens = readTokens(file);
        if (tokens.isEmpty()) {

            plugin.saveConfig();
            return;

        }

        FileConfiguration config = plugin.getConfig();
        Map<String, Object> substituted = new LinkedHashMap<>();
        for (Map.Entry<String, String> token : tokens.entrySet()) {

            substituted.put(token.getKey(), config.get(token.getKey()));
            config.set(token.getKey(), token.getValue());

        }

        try {

            plugin.saveConfig();

        } finally {

            // Restore the resolved values for the running server.
            for (Map.Entry<String, Object> value : substituted.entrySet()) {

                config.set(value.getKey(), value.getValue());

            }

        }

    }

}
