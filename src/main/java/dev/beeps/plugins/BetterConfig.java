package dev.beeps.plugins;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class BetterConfig {

    BetterKeepInventory plugin;
    FileConfiguration config;

    public enum ItemMode {
        NONE,
        SIMPLE,
        PERCENTAGE
    }

    public enum ExpMode {
        NONE,
        ALL,
        SIMPLE,
        PERCENTAGE
    }

    public enum HungerMode {
        NONE,
        KEEP,
    }

    public BetterConfig(BetterKeepInventory _plugin, FileConfiguration _config){

        // set instance to config object
        config = _config;
        plugin = _plugin;

        switch(config.getInt("main.config_version", -1)){

            case -1:
            case 0:
            case 1:
                migrateToConfigClass();
                break;

        }

    }

    public void resetConfig() {
        File file = new File(plugin.getDataFolder() + File.separator + "config.yml");
        if (file.exists()) {
            file.delete();
        }

        plugin.saveDefaultConfig();
    }

    public void migrateToConfigClass() {

        plugin.log(Level.INFO, "ConfigMigrator", "Migrating to config format 2");

        // General
        File file = new File(plugin.getDataFolder() + File.separator + "config.yml");
        if (file.exists()){


            // load old settings
            boolean enabled = config.getBoolean("enabled");
            boolean enable_item_damage = config.getBoolean("enable_item_damage");
            boolean ignore_enchants = config.getBoolean("ignore_enchants");
            boolean dont_break_items = config.getBoolean("dont_break_items");

            double min_damage_pct = config.getDouble("min_damage_pct");
            double max_damage_pct = config.getDouble("max_damage_pct");

            boolean enable_xp_loss = config.getBoolean("enable_xp_loss");
            boolean xp_reset_current_level_progress = config.getBoolean("xp_reset_current_level_progress");
            double min_xp_loss_pct = config.getDouble("min_xp_loss_pct");
            double max_xp_loss_pct = config.getDouble("max_xp_loss_pct");

            boolean keep_hunger_level = config.getBoolean("keep_hunger_level");
            int keep_hunger_level_min = config.getInt("keep_hunger_level_min");
            int keep_hunger_level_max = config.getInt("keep_hunger_level_max");

            // rename config to old config, just incase
            boolean rDone = file.renameTo(new File(plugin.getDataFolder() + File.separator + "config.old.yml"));
            if(!rDone){
                plugin.log(Level.INFO, "ConfigMigrator", "Moving old config file failed, Disabling plugin!");
                plugin.getPluginLoader().disablePlugin(plugin);
                return;
            }else{
                plugin.log(Level.INFO, "ConfigMigrator", "Moved old config file to 'config.old.yml'");
            }

            plugin.saveDefaultConfig();
            plugin.saveConfig();
            plugin.log(Level.INFO, "ConfigMigrator", "Saved new config to 'config.yml'");

            config = plugin.getConfig();

            config.set("main.enabled", enabled);
            config.set("main.debug", false);
            config.set("main.config_version", 2);

            config.set("items.hotbar.mode", enable_item_damage ? "PERCENTAGE" : "NONE");
            config.set("items.inventory.mode", enable_item_damage ? "PERCENTAGE" : "NONE");
            config.set("items.armor.mode", enable_item_damage ? "PERCENTAGE" : "NONE");

            config.set("items.hotbar.use_enchantments", !ignore_enchants);
            config.set("items.inventory.use_enchantments", !ignore_enchants);
            config.set("items.armor.use_enchantments", !ignore_enchants);

            config.set("items.hotbar.dont_break", dont_break_items);
            config.set("items.inventory.dont_break", dont_break_items);
            config.set("items.armor.dont_break", dont_break_items);

            config.set("items.hotbar.min", min_damage_pct);
            config.set("items.inventory.min", min_damage_pct);
            config.set("items.armor.min", min_damage_pct);

            config.set("items.hotbar.max", max_damage_pct);
            config.set("items.inventory.max", max_damage_pct);
            config.set("items.armor.max", max_damage_pct);

            config.set("exp.levels.mode", enable_xp_loss ? "PERCENTAGE" : "NONE");
            config.set("exp.levels.min", min_xp_loss_pct);
            config.set("exp.levels.max", max_xp_loss_pct);
            config.set("exp.reset_level", xp_reset_current_level_progress);

            config.set("hunger.mode", keep_hunger_level ? "KEEP" : "NONE");
            config.set("hunger.min", keep_hunger_level_min);
            config.set("hunger.max", keep_hunger_level_max);

            plugin.log(Level.INFO, "ConfigMigrator", "Applied values of old config to new config");

            config.set("enabled", null);
            config.set("enable_item_damage", null);
            config.set("ignore_enchants", null);
            config.set("dont_break_items", null);
            config.set("min_damage_pct", null);
            config.set("max_damage_pct", null);
            config.set("enable_xp_loss", null);
            config.set("xp_reset_current_level_progress", null);
            config.set("min_xp_loss_pct", null);
            config.set("max_xp_loss_pct", null);
            config.set("keep_hunger_level", null);
            config.set("keep_hunger_level_min", null);
            config.set("keep_hunger_level_max", null);

            config.options().header("Config was migrated, Please refer to https://beeps.dev/better-keep-inventory/betterkeepinventory-13 to learn more");

            plugin.log(Level.INFO, "ConfigMigrator", "Removed old keys from config");

            plugin.saveConfig();
            plugin.log(Level.INFO, "ConfigMigrator", "Saved new config");
            plugin.log(Level.INFO, "ConfigMigrator", "Completed migration to format 2");

        }else{
            resetConfig();
            plugin.log(Level.INFO, "ConfigMigrator", "Could not find a config to migrate, Generating new one");
        }

    }

    public boolean getBoolean(String path){
        return this.config.getBoolean(path);
    }
    public boolean getBoolean(String path, boolean fallback){
        return this.config.getBoolean(path, fallback);
    }

    public int getInt(String path){
        return this.config.getInt(path);
    }

    public int getInt(String path, int fallback){
        return this.config.getInt(path, fallback);
    }

    public double getDouble(String path){
        return this.config.getDouble(path);
    }

    public double getDouble(String path, double fallback){
        return this.config.getDouble(path, fallback);
    }

    public String getString(String path){
        return this.config.getString(path);
    }

    public String getString(String path, String fallback){
        return this.config.getString(path, fallback);
    }

    public List<?> getList(String path){
        return this.config.getList(path);
    }

    public List<?> getList(String path, List<?> fallback){
        return this.config.getList(path, fallback);
    }

    public List<Material> getMaterialList(String path){
        List<String> list = (List<String>) this.config.getList(path);
        List<Material> materials = new ArrayList<Material>();

        assert list != null;
        for(String material : list) {
            materials.add(Material.getMaterial(material));
        }

        return materials;
    }
    public List<PotionEffectType> getEffectList(String path){
        List<String> list = (List<String>) this.config.getList(path);
        List<PotionEffectType> effects = new ArrayList<>();

        assert list != null;
        for(String effect : list) {
            effects.add(PotionEffectType.getByName(effect));
        }

        return effects;
    }

    public ItemMode getItemMode(String path){
        String value = this.getString(path, ItemMode.NONE.toString());
        return ItemMode.valueOf(value);
    }

    public ExpMode getExpMode(String path){
        String value = this.getString(path, ExpMode.NONE.toString());
        return ExpMode.valueOf(value);
    }

    public HungerMode getHungerMode(String path){
        String value = this.getString(path, HungerMode.NONE.toString());
        return HungerMode.valueOf(value);
    }

}
