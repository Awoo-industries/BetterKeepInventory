package dev.beeps.plugins;

import dev.beeps.plugins.Depends.Towny;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

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
            case 2:
                migrateTo1p4();
                break;

        }

    }

    public BetterConfig reload(){
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        return this;
    }

    public boolean moveConfigToOld(File file){

        // rename config to old config, just incase
        boolean rDone = file.renameTo(new File(plugin.getDataFolder() + File.separator + "config.old.yml"));
        if(!rDone){
            plugin.log(Level.INFO, "ConfigMigrator", "Moving old config file failed, Disabling plugin!");
            plugin.getPluginLoader().disablePlugin(plugin);
            return false;
        }else{
            plugin.log(Level.INFO, "ConfigMigrator", "Moved old config file to 'config.old.yml'");
            return true;
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

            if(!moveConfigToOld(file)){
                return;
            };

            plugin.saveDefaultConfig();
            plugin.getConfig().options().copyDefaults();
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

            config.options().header("Config was migrated, Please refer to https://beeps.notion.site/BetterKeepInventory-1-3-d84a385719e24060a4aff65baa3d9443 to learn more");

            plugin.log(Level.INFO, "ConfigMigrator", "Removed old keys from config");

            plugin.saveConfig();
            plugin.log(Level.INFO, "ConfigMigrator", "Saved new config");
            plugin.log(Level.INFO, "ConfigMigrator", "Completed migration to format 2");

        }else{
            resetConfig();
            plugin.log(Level.INFO, "ConfigMigrator", "Could not find a config to migrate, Generating new one");
        }

    }

    public void migrateTo1p4(){

        plugin.log(Level.INFO, "ConfigMigrator", "Migrating to config format 3");
        File file = new File(plugin.getDataFolder() + File.separator + "config.yml");

        if(!moveConfigToOld(file)){
            return;
        };

        config.set("main.config_version", 3);
        config.set("main.grace", false); // include grace

        // Worlds
        String[] pvp_disabled = {"ITEMS"};
        config.set("overrides.worlds.world_no_pvp.pvp", pvp_disabled);

        String[] all_disabled = {"ALL"};
        config.set("overrides.worlds.world_disabled", all_disabled);

        String[] no_exp_loss = {"EXP", "EXP_LEVEL"};
        config.set("overrides.worlds.world_no_exp_loss", no_exp_loss);

        // Towny
        String[] wild = {"NONE"};
        String[] any = {"ECO"};
        String[] player = {"ITEMS"};
        String[] custom = {"ITEMS", "HUNGER"};

        config.set("overrides.towny.enabled", false);

        config.set("overrides.towny.towns.wilderness", wild);
        config.set("overrides.towny.towns.any_town", any);
        config.set("overrides.towny.towns.player_town", player);
        config.set("overrides.towny.towns.North Appletown", custom);

        config.set("overrides.towny.nations.any_nation", any);
        config.set("overrides.towny.nations.player_nation", player);
        config.set("overrides.towny.nations.The Empire", custom);

        plugin.saveConfig();
        plugin.log(Level.INFO, "ConfigMigrator", "Saved new config");
        plugin.log(Level.INFO, "ConfigMigrator", "Completed migration to format 3");

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

    public List<String> getDisabledModesInWorld(String world){
        plugin.log(Level.FINE, "GetOverrideForMode->GetPath:" + "overrides.worlds." + world);
        List<String> worldlist = this.config.getStringList("overrides.worlds." + world);
        List<String> anylist = this.config.getStringList("overrides.worlds.all");
        return Stream.concat(worldlist.stream(), anylist.stream()).toList();
    }
    public List<String> getDisabledModesInWorldByDamageType(String world, String damageType){
        plugin.log(Level.FINE, "GetOverrideForMode->GetPath:" + "overrides.worlds." + world);
        List<String> worldlist = this.config.getStringList("overrides.worlds." + world + ".damage_types." + damageType);
        List<String> anylist = this.config.getStringList("overrides.worlds.all.damage_types." + damageType);
        return Stream.concat(worldlist.stream(), anylist.stream()).toList();
    }
    public List<String> getDisabledModesInTown(String town_name){
        plugin.log(Level.FINE, "GetOverrideForMode->GetPath:" + "overrides.towny.towns." + town_name);
        return this.config.getStringList("overrides.towny.towns." + town_name);
    }
    public List<String> getDisabledModesInNation(String nation_name){
        plugin.log(Level.FINE, "GetOverrideForMode->GetPath:" + "overrides.towny.nations." + nation_name);
        return this.config.getStringList("overrides.towny.nations." + nation_name);
    }

    public boolean GetOverrideForMode(String mode, Player ply){

        plugin.log(Level.FINE, ply, "GetOverrideForMode->Mode:" + mode);

        World world = ply.getWorld();

        if(getDisabledModesInWorld(world.getName()).contains(mode)){
            plugin.log(Level.FINE, ply, "GetOverrideForMode->World:" + world.getName());
            return true;
        }

        EntityDamageEvent dmg_event = ply.getLastDamageCause();
        if(dmg_event != null){

            String event = String.valueOf(dmg_event.getCause());

            if(dmg_event instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent edmg_event = (EntityDamageByEntityEvent) dmg_event;
                if(edmg_event.getDamager() instanceof Player){
                    event = "PVP";
                }
            }

            plugin.log(Level.FINE, ply, "TestOverride->World:damage_type:" + event);

            if(getDisabledModesInWorldByDamageType(world.getName(), event).contains(mode)){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->World:damage_type:" + event);
                return true;
            }
            if(getDisabledModesInWorldByDamageType(world.getName(), "ANY").contains(mode)){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->World:damage_type:ANY");
                return true;
            }
        }

        if(config.getBoolean("overrides.towny.enabled") && plugin.checkDependency("Towny")){

            Towny tl = new Towny(ply.getLocation());
            Towny tp = new Towny(ply);

            // Town of player
            if(getDisabledModesInTown("player_town").contains(mode) && Objects.equals(tl.getTownName(), tp.getTownName())){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->Towny:player_town");
                return true;
            }

            // Town at location of death
            if(getDisabledModesInTown(tl.getTownName()).contains(mode)){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->Towny:town:" + tl.getTownName());
                return true;
            }

            // Nation at location of death
            if(getDisabledModesInTown("any_town").contains(mode) && tl.getTownName() != null){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->Towny:town:any_town");
                return true;
            }

            // Nation of player
            if(getDisabledModesInNation("player_nation").contains(mode) && Objects.equals(tl.getNationName(), tp.getNationName()) && tl.getNation() != null){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->Towny:nation:player_nation");
                return true;
            }

            // Nation at location of death
            if(getDisabledModesInNation(tl.getNationName()).contains(mode)){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->Towny:nation:" + tl.getNationName());
                return true;
            }

            // Nation at location of death
            if(getDisabledModesInNation("any_nation").contains(mode) && tl.getNationName() != null){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->Towny:nation:any_nation");
                return true;
            }

            // Wilderness
            if(getDisabledModesInTown("wilderness").contains(mode) && tl.getTownName() == null){
                plugin.log(Level.FINE, ply, "GetOverrideForMode->Towny:wilderness");
                return true;
            }

        }

        plugin.log(Level.FINE, ply, "GetOverrideForMode->None");
        return false;

    }

}
