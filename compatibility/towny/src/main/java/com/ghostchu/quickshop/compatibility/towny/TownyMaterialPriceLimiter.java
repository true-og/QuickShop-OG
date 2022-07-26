package com.ghostchu.quickshop.compatibility.towny;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TownyMaterialPriceLimiter {
    private final Map<Material, Double> prices = new HashMap<>();

    public TownyMaterialPriceLimiter(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            Material mat = Material.matchMaterial(key);
            if(mat == null)
                Main.getPlugin(Main.class).getLogger().warning("Invalid material in config: " + key);
            double price = section.getDouble(key);
            prices.put(mat, price);
        }
    }

    @Nullable
    public Double getPrice(@NotNull Material material){
        return prices.get(material);
    }

}
