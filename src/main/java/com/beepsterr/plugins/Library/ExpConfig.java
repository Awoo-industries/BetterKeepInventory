package com.beepsterr.plugins.Library;

import org.bukkit.configuration.ConfigurationSection;

public class ExpConfig {

    public enum Mode {
        SIMPLE, PERCENTAGE, ALL
    }

    public enum How {
        DELETE, DROP
    }

    private Mode mode;
    private How how;
    private float min;
    private float max;

    public ExpConfig(ConfigurationSection dropConfig) {
        mode = ExpConfig.Mode.valueOf(dropConfig.getString("mode", "PERCENTAGE").toUpperCase());
        how = ExpConfig.How.valueOf(dropConfig.getString("how", "DROP").toUpperCase());
        min = (float)dropConfig.getDouble("min", 0.0);
        max = (float)dropConfig.getDouble("max", 0.0);
    }

    public Mode getMode() {
        return mode;
    }

    public How getHow() {
        return how;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

}
