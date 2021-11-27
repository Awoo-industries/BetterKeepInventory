package dev.beeps.plugins;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

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
        KEEP
    }

    public BetterConfig(BetterKeepInventory _plugin, FileConfiguration _config){

        // set instance to config object
        config = _config;
        plugin = _plugin;

        switch(config.getInt("main.config_version", -1)){

            case -1:
                resetConfig();
                break;

            case 0:
            case 1:
                migrateToConfigClass();
                break;

        }

    }

    public void resetConfig() {
        plugin.saveDefaultConfig();
    }

    public void migrateToConfigClass() {
        throw new NotImplementedException("CONFIG_MIGRATION_NOT_IMPLEMENTED");
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

    public ItemMode getItemMode(String path){
        String value = this.getString(path, ItemMode.NONE.toString());
        return ItemMode.valueOf(value);
    }

}
