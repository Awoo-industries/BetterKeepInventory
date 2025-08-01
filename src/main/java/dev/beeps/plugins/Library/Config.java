package dev.beeps.plugins.Library;

import dev.beeps.plugins.BetterKeepInventory;
import dev.beeps.plugins.Exceptions.ConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public enum DefaultBehavior {
        INHERIT, DROP, KEEP
    }

    private static Config instance;

    private final String version;
    private final String hash;
    private final boolean debug;
    private final DefaultBehavior defaultBehavior;
    private List<ConfigRule> rules;

    public Config(FileConfiguration config) throws ConfigurationException {
        instance = this;

        BetterKeepInventory.getInstance().getLogger().info("Parsing Config");

        version = config.getString("version");
        hash = config.getString("hash");
        debug = config.getBoolean("debug");
        defaultBehavior = DefaultBehavior.valueOf(config.getString("default_behavior").toUpperCase());

        // Parse rules section
        rules = new ArrayList<>();
        ConfigurationSection rulesSection = config.getConfigurationSection("rules");
        if (rulesSection != null) {
            for (String ruleKey : rulesSection.getKeys(false)) {
                ConfigurationSection ruleSection = rulesSection.getConfigurationSection(ruleKey);
                if (ruleSection != null) {
                    rules.add(new ConfigRule(ruleSection));
                }
            }
        }
    }

    public static Config getInstance() {
        return instance;
    }

    public String getVersion() {
        return version;
    }

    public String getHash() {
        return hash;
    }

    public boolean isDebug() {
        return debug;
    }

    public DefaultBehavior getDefaultBehavior() {
        return defaultBehavior;
    }

    public List<ConfigRule> getRules() {
        return rules;
    }
}
