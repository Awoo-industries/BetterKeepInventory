package dev.beeps.plugins.Events.Types;

import dev.beeps.plugins.BetterConfig;
import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class ItemHandler {

    public enum SlotType {
        inventory,
        armor,
        hotbar
    }

    String config_prefix;

    public ItemHandler(BetterKeepInventory plugin, Player ply, ItemStack item, SlotType slot, int slotIndex){

        BetterConfig config = plugin.config;
        config_prefix = String.format("items.%s", slot.toString());
        plugin.log(Level.FINE, ply, "ItemHandler->START");
        plugin.log(Level.FINE, ply, "Item: " + item.getType());
        plugin.log(Level.FINE, ply, "Slot Type: " + slot + " ("+slotIndex+")");

        BetterConfig.ItemMode mode = config.getItemMode(path("mode"));
        double min = config.getDouble(path("min"), 0);
        double max = config.getDouble(path("max"), 0);

        boolean use_enchantments = config.getBoolean(path("use_enchantments"), true);
        boolean dont_break = config.getBoolean(path("dont_break"), true);

        List<Material> ignored_materials = config.getMaterialList(path("ignored_materials"));

        ItemMeta meta = item.getItemMeta();
        Material type = item.getType();

        if(ignored_materials.contains(type)){
            plugin.log(Level.FINE, ply, "Exited Early: Item is in ignored_materials");
            return;
        }

        if(meta != null){
            String ingored_name = config.getString(path("ignored_name"), "NONE");
            String ignored_lore = config.getString(path("ignored_lore"), "NONE");

            if(!Objects.equals(ingored_name, "NONE") && meta.getDisplayName().toLowerCase().contains(ingored_name.toLowerCase())){
                plugin.log(Level.FINE, ply, "Exited Early: Item contains ignored name");
                return;
            }

            if(!Objects.equals(ignored_lore, "NONE") && meta.getLore() != null) {
                for (String lore : Objects.requireNonNull(meta.getLore())) {
                    if (lore.toLowerCase().contains(ignored_lore.toLowerCase())) {
                        plugin.log(Level.FINE, ply, "Exited Early: Item contains ignored lore");
                        return;
                    }
                }
            }
        }



        if(meta instanceof Damageable damageableMeta) {

            int damageDealt = 0;

            plugin.log(Level.FINE, ply, "Using Damage mode " + mode);
            switch(mode){
                case NONE:
                    return;
                case SIMPLE:
                    damageDealt = (int) (min + (max - min) * plugin.randomizer.nextDouble());
                    break;
                case PERCENTAGE:
                    double roll = Math.floor(Math.random()*(max-min+1)+min);
                    damageDealt = (int)(((double)type.getMaxDurability()/100) * roll);
                    break;
            }


            plugin.log(Level.FINE, ply, "initialDamageCalc: " + damageDealt);

            // Vanishing Curse
            if( use_enchantments && item.getEnchantments().containsKey(Enchantment.VANISHING_CURSE)){
                plugin.log(Level.FINE, ply, "Found vanishing curse!!!");
                item.setAmount(item.getAmount() - 1);
            }

            // Unbreaking Enchant
            if( use_enchantments && item.getEnchantments().containsKey(Enchantment.DURABILITY)){

                int level = item.getEnchantmentLevel(Enchantment.DURABILITY);
                if(level > 9){
                    damageDealt = 0;
                }

                damageDealt = damageDealt / (level+1);
            }

            // bStats flair
            int reportedDamageDealt = Math.max(0, Math.min(damageDealt, type.getMaxDurability() - damageableMeta.getDamage()));
            plugin.durabilityPointsLost += reportedDamageDealt;


            if( ( type.getMaxDurability() - damageableMeta.getDamage()) - damageDealt < 0){

                if( dont_break ){

                    plugin.log(Level.FINE, ply, "Item was left with 0 durability.");
                    damageableMeta.setDamage(type.getMaxDurability());
                    item.setItemMeta(meta);

                }else{

                    plugin.log(Level.FINE, ply, "Item was destroyed.");

//                    ply.getInventory().setItem(slotIndex, new ItemStack(Material.AIR));
                    item.setAmount(item.getAmount() - 1);
                    damageableMeta.setDamage(0);
                    item.setItemMeta(meta);

                    ply.getWorld().playSound(ply.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8f, 0.8f);
                }

            }else{

                // set damage
                damageableMeta.setDamage(damageableMeta.getDamage() + damageDealt);
                item.setItemMeta(meta);

            }

        }
    }

    private String path(String path){
        return String.format("%s.%s", config_prefix, path);
    }

}
