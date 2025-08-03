package com.beepsterr.plugins.Effects.Configs;

import org.bukkit.configuration.ConfigurationSection;

public class EconomyConfig {

    public enum Mode {
        SIMPLE, PERCENTAGE
    }

    private final Mode mode;
    private final double min;
    private final double max;
    private boolean allowNegativeBalance;

    public EconomyConfig(ConfigurationSection dropConfig) {
        min = dropConfig.getDouble("min", 0.0);
        max = dropConfig.getDouble("max", 0.0);
        allowNegativeBalance = dropConfig.getBoolean("allow_negative_balance", false);
        mode = Mode.valueOf(dropConfig.getString("mode", "DAMAGE_PERCENTAGE").toUpperCase());
    }

    public double getMin() {
        return min;
    }
    public double getMax() {
        return max;
    }
    public boolean isNegativeBalanceAllowed() {
        return allowNegativeBalance;
    }
    public Mode getMode() {
        return mode;
    }

}
