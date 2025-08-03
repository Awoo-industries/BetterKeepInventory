package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Library.Types.MaterialType;
import org.bukkit.configuration.ConfigurationSection;

public class DropItemsConfig {

    public enum Mode {
        SIMPLE, PERCENTAGE, ALL
    }

    private Mode mode;
    private float min;
    private float max;
    private MaterialType items;

    public DropItemsConfig(ConfigurationSection dropConfig) {
        mode = DropItemsConfig.Mode.valueOf(dropConfig.getString("mode", "DAMAGE_PERCENTAGE").toUpperCase());
        min = (float)dropConfig.getDouble("min", 0.0);
        max = (float)dropConfig.getDouble("max", 0.0);

        if(dropConfig.isList("items")) {
            items = new MaterialType(dropConfig.getStringList("items"));
        }else{
            BetterKeepInventory.getInstance().getLogger().warning("Configuration Error: 'items' must be a list @ " + dropConfig.getCurrentPath());
        }
    }

    public Mode getMode() {
        return mode;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public MaterialType getItems() {
        return items;
    }
}
