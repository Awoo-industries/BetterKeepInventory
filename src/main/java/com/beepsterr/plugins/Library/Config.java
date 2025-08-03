package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public enum DefaultBehavior {
        INHERIT, DROP, KEEP
    }

    private static Config instance;

    private final FileConfiguration rawConfig;
    private final String version;
    private final String hash;
    private final boolean debug;
    private final DefaultBehavior defaultBehavior;
    private List<ConfigRule> rules;

    public Config(FileConfiguration config) throws ConfigurationException {

        instance = this;
        this.rawConfig = config;

        BetterKeepInventory.getInstance().log("Loading configuration from " + config.getCurrentPath());

        version = config.getString("version", "2.0.0");
        hash = config.getString("hash", "OLD");
        debug = config.getBoolean("debug", false);

        MigrateConfiguration();

        defaultBehavior = DefaultBehavior.valueOf(config.getString("default_behavior", "INHERIT").toUpperCase());

        // Parse rules section
        rules = new ArrayList<>();
        ConfigurationSection rulesSection = config.getConfigurationSection("rules");
        if (rulesSection != null) {
            for (String ruleKey : rulesSection.getKeys(false)) {
                ConfigurationSection ruleSection = rulesSection.getConfigurationSection(ruleKey);
                if (ruleSection != null) {
                    rules.add(new ConfigRule(ruleSection, null));
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

    public boolean isDebug() {
        return debug;
    }

    public DefaultBehavior getDefaultBehavior() {
        return defaultBehavior;
    }

    public List<ConfigRule> getRules() {
        return rules;
    }

    public void MigrateConfiguration() throws ConfigurationException {

        // Detect pre 2.0 configuration files
        int legacyConfigVersion = rawConfig.getInt("main.config_version", 0);
        if(legacyConfigVersion > 0){
            throw new ConfigurationException("main.config_version",
                    "Detected a legacy configuration file, refusing to load it.\n" +
                            "Please read the migration instructions at:\n" +
                            "https://beeps.notion.site/Migrating-to-2-0-244f258220598076bbbacc7af661f068"
            );
        }

        String installedVersion = Version.getVersionWithoutChannel();
        String configVersion = this.version;

        if(!installedVersion.equals(configVersion)){
            // ... Create migrations here when needed
        }
    }
}
