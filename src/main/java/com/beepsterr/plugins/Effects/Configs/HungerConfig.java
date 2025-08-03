package com.beepsterr.plugins.Effects.Configs;

import org.bukkit.configuration.ConfigurationSection;

public class HungerConfig {

    private final int min;
    private final int amount;

    public HungerConfig(ConfigurationSection dropConfig) {
        min = dropConfig.getInt("min", 0);
        amount = dropConfig.getInt("amount", 0);
    }

    public int getMin() {
        return min;
    }

    public int getAmount() {
        return amount;
    }

}
