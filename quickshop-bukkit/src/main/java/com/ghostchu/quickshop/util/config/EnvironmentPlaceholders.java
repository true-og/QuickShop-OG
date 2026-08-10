package com.ghostchu.quickshop.util.config;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

// Keeps SubstAgent placeholders on disk when the config is written back.
// SubstAgent expands ^{ENV_VAR} while the server reads a config file, so the
// value held in memory is already resolved and saving it would burn the secret
// into the file. The live file cannot be re-read to recover the token, because
// SubstAgent intercepts the stream and the NIO read paths alike, so the token
// names come from the default config inside the plugin jar instead. A jar is
// not a config file, so that copy still carries the raw placeholders.
public final class EnvironmentPlaceholders {

    // The ^{ENV_VAR} form SubstAgent expands, group 1 is the variable name.
    private static final Pattern PLACEHOLDER = Pattern.compile("^\\^\\{([A-Za-z_][A-Za-z0-9_]*)}$");

    private EnvironmentPlaceholders() {

    }

    // Maps each config key the bundled default declares to its variable name.
    @NotNull
    public static Map<String, String> readBundledPlaceholders(@NotNull JavaPlugin plugin) {

        InputStream resource = plugin.getResource("config.yml");
        if (resource == null) {

            return Collections.emptyMap();

        }

        YamlConfiguration bundled;
        try (InputStreamReader reader = new InputStreamReader(new BufferedInputStream(resource),
                StandardCharsets.UTF_8))
        {

            bundled = YamlConfiguration.loadConfiguration(reader);

        } catch (Exception e) {

            return Collections.emptyMap();

        }

        Map<String, String> placeholders = new LinkedHashMap<>();
        for (String key : bundled.getKeys(true)) {

            Object value = bundled.get(key);
            if (!(value instanceof String string)) {

                continue;

            }

            Matcher matcher = PLACEHOLDER.matcher(string.trim());
            if (matcher.matches()) {

                placeholders.put(key, matcher.group(1));

            }

        }

        return placeholders;

    }

    // Saves the config without persisting substituted placeholder values.
    public static void saveConfig(@NotNull JavaPlugin plugin) {

        FileConfiguration config = plugin.getConfig();
        Map<String, Object> resolved = new LinkedHashMap<>();
        for (Map.Entry<String, String> placeholder : readBundledPlaceholders(plugin).entrySet()) {

            String variable = System.getenv(placeholder.getValue());
            if (variable == null) {

                continue;

            }

            Object current = config.get(placeholder.getKey());
            // Only a value equal to the variable can have come from SubstAgent.
            if (!(current instanceof String string) || !string.equals(variable)) {

                continue;

            }

            resolved.put(placeholder.getKey(), current);
            config.set(placeholder.getKey(), "^{" + placeholder.getValue() + "}");

        }

        try {

            plugin.saveConfig();

        } finally {

            // Restore the resolved values for the running server.
            for (Map.Entry<String, Object> value : resolved.entrySet()) {

                config.set(value.getKey(), value.getValue());

            }

        }

    }

}
