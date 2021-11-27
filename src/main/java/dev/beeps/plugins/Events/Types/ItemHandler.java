package dev.beeps.plugins.Events.Types;

import dev.beeps.plugins.BetterConfig;
import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class ItemHandler {

    public enum SlotType {
        inventory,
        armor,
        hotbar
    }

    String config_prefix;

    public ItemHandler(BetterKeepInventory plugin, Player ply, ItemStack item, SlotType slot){

        BetterConfig config = plugin.config;
        config_prefix = String.format("items.%s", slot.toString());

        BetterConfig.ItemMode mode = config.getItemMode(path("mode"));
        double min = config.getDouble(path("min"), 0);
        double max = config.getDouble(path("max"), 0);

        boolean use_enchantments = config.getBoolean(path("use_enchantments"), true);
        boolean dont_break = config.getBoolean(path("dont_break"), true);

        String ingored_name = config.getString(path("name"), "NONE");
        String ignored_lore = config.getString(path("lore"), "NONE");

        List<Material> ignored_materials = config.getMaterialList(path("ignored_materials"));

        if(ignored_materials.contains(item.getType())){
            plugin.log(Level.FINE, ply, "ItemHandler->Ignored:ignored_material");
            return;
        }


    }

    private String path(String path){
        return String.format("%s.%s2", config_prefix, path);
    }

}
