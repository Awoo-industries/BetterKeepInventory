package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.Library.Types.MaterialType;
import com.beepsterr.plugins.Library.Types.SlotType;
import org.bukkit.configuration.ConfigurationSection;

public class DamageItemsConfig {

    public enum Mode {
        PERCENTAGE, PERCENTAGE_REMAINING, SIMPLE
    }

    private Mode mode;
    private float min;
    private float max;
    private boolean useEnchantments;
    private boolean dontBreak;
    private SlotType slots;
    private MaterialType items;

    public DamageItemsConfig(ConfigurationSection damageConfig) {
        // Parse damage configuration
        mode = Mode.valueOf(damageConfig.getString("mode", "DAMAGE_PERCENTAGE").toUpperCase());
        min = (float)damageConfig.getDouble("min", 0.0);
        max = (float)damageConfig.getDouble("max", 0.0);
        useEnchantments = damageConfig.getBoolean("use_enchantments", false);
        dontBreak = damageConfig.getBoolean("dont_break", false);
        slots = new SlotType(damageConfig.getStringList("slots"));
        items = new MaterialType(damageConfig.getStringList("items"));
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

    public boolean isUseEnchantments() {
        return useEnchantments;
    }

    public boolean isDontBreak() {
        return dontBreak;
    }

    public SlotType getSlots() {
        return slots;
    }

    public MaterialType getItems() {
        return items;
    }
}