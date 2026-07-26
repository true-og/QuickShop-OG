package com.ghostchu.quickshop.api.inventory;

import com.google.common.collect.MapMaker;
import java.util.Map;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryWrapperRegistry {

    private final Map<String, InventoryWrapperManager> registry = new MapMaker().makeMap();

    // Extra provider names resolving onto a canonical registry key.
    private final Map<String, String> aliases = new MapMaker().makeMap();

    @Nullable
    public String find(InventoryWrapperManager manager) {

        for (Map.Entry<String, InventoryWrapperManager> entry : registry.entrySet()) {

            if (entry.getValue() == manager || entry.getValue().equals(manager)) {

                return entry.getKey();

            }

        }

        return null;

    }

    @Nullable
    public InventoryWrapperManager get(String providerName) {

        InventoryWrapperManager manager = registry.get(providerName);
        if (manager != null) {

            return manager;

        }

        String canonical = aliases.get(providerName);
        return canonical == null ? null : registry.get(canonical);

    }

    /**
     * Makes an extra provider name resolve to an already registered one. Aliases
     * are only read by {@link #get(String)} and are never returned by
     * {@link #find(InventoryWrapperManager)}, so they never get persisted.
     *
     * @param alias         The extra name to accept.
     * @param canonicalName The registered name it resolves to.
     */
    public void registerAlias(@NotNull String alias, @NotNull String canonicalName) {

        aliases.put(alias, canonicalName);

    }

    public void register(@NotNull Plugin plugin, @NotNull InventoryWrapperManager manager) {

        if (registry.containsKey(plugin.getName())) {

            plugin.getLogger()
                    .warning("Nag Author: Plugin " + plugin.getName()
                            + " already have a registered InventoryWrapperManager: "
                            + registry.get(plugin.getName()).getClass().getName()
                            + " but trying register another new manager: " + manager.getClass().getName()
                            + ". This may cause unexpected behavior! Replacing with new instance...");

        }

        registry.put(plugin.getName(), manager);

    }

    /**
     * Registers a manager under an explicit provider name instead of a plugin name.
     * The name given here is what {@link #find(InventoryWrapperManager)} returns
     * and therefore what gets persisted on shops.
     *
     * @param providerName The name to register under.
     * @param manager      The manager.
     */
    public void register(@NotNull String providerName, @NotNull InventoryWrapperManager manager) {

        registry.put(providerName, manager);

    }

    public void unregister(@NotNull Plugin plugin) {

        registry.remove(plugin.getName());

    }

    /**
     * Removes a manager registered through
     * {@link #register(String, InventoryWrapperManager)}.
     *
     * @param providerName The name it was registered under.
     */
    public void unregister(@NotNull String providerName) {

        registry.remove(providerName);

    }

}
