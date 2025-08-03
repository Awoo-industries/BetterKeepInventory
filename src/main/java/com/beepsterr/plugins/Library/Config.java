package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import com.beepsterr.plugins.Library.Versions.Version;
import com.beepsterr.plugins.Library.Versions.VersionChannel;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public class Config {

    public enum DefaultBehavior {
        INHERIT, DROP, KEEP
    }

    private static Config instance;

    private final FileConfiguration rawConfig;
    private FileConfiguration rawMessages;
    private final String version;
    private final VersionChannel notifyChannel;
    private final String hash;
    private final boolean debug;
    private final DefaultBehavior defaultBehavior;
    private List<ConfigRule> rules;

    public Config(FileConfiguration config) throws ConfigurationException {

        BetterKeepInventory plugin = BetterKeepInventory.getInstance();

        instance = this;
        this.rawConfig = config;

        plugin.saveResource("messages.yml", false);
        this.rawMessages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));

        plugin.log("Loading configuration from " + config.getCurrentPath());

        version = config.getString("version", "2.0.0");
        notifyChannel = VersionChannel.valueOf(config.getString("notify_channel", "STABLE").toUpperCase());
        hash = config.getString("hash", "OLD");
        debug = config.getBoolean("debug", false);

        try{
            LoadMessages(plugin);
        }catch (IOException e){
            throw new ConfigurationException("messages.yml", "Failed to load messages file: " + e.getMessage());
        }

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

    public VersionChannel getNotifyChannel() {
        return notifyChannel;
    }

    public String getMessage(String key, Map<String, String> replacements) {
        if(rawMessages == null){
            BetterKeepInventory.getInstance().log("Messages not loaded?? Check if messages.yml exists.");
            return key;
        }

        if(!rawMessages.contains(key)){
            BetterKeepInventory.getInstance().log("Did not find message with key: " + key);
            return key;
        }

        String message = rawMessages.getString(key, key);
        if (replacements != null) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(Player ply, String key, Map<String, String> replacements) {
        String message = getMessage(key, replacements);
        if(!message.isEmpty()){
            ply.sendMessage(message);
        }
    }

    public void LoadMessages(BetterKeepInventory plugin) throws IOException {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(file);

        // Load default from jar
        InputStream defConfigStream = plugin.getResource("messages.yml");
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));

        // Copy missing keys
        for (String key : defaultConfig.getKeys(true)) {
            if (!userConfig.contains(key)) {
                userConfig.set(key, defaultConfig.get(key));
            }
        }

        // Save updated file
        userConfig.save(file);
        this.rawMessages = userConfig;
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
        if(!installedVersion.equals(this.version)){
            // ... Create migrations here when needed
        }

        // Migration completed, write new changes
        rawConfig.set("version", installedVersion);
        rawConfig.set("hash", Version.getCommitHash());
        BetterKeepInventory.getInstance().saveConfig();
    }
}
