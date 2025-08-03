package com.beepsterr.plugins.Effects;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Library.ConfigRule;
import com.beepsterr.plugins.Library.DamageItemsConfig;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class DamageItemEffect extends Effect {

    DamageItemsConfig damage;

    public DamageItemEffect(ConfigRule rule, DamageItemsConfig damage){
        super(rule);
        this.damage = damage;
    }

    @Override
    public void runRespawn(Player ply, PlayerRespawnEvent event) {

    }

    @Override
    public void runDeath(Player ply, PlayerDeathEvent event) {

        BetterKeepInventory plugin = BetterKeepInventory.getInstance();
        Random rng = plugin.rng;
        List<Integer> slots = damage.getSlots().getSlotIds();
        List<Material> items = damage.getItems().getMaterials();
        // loop over the players inventory


        // loop over the players inventory
        for (int i = 0; i < ply.getInventory().getSize(); i++) {
            // get the item in the slot
            var item = ply.getInventory().getItem(i);
            if (item != null && items.contains(item.getType())) {
                ItemMeta meta = item.getItemMeta();
                if(meta instanceof Damageable damageableMeta){

                    if(!slots.contains(i)) continue;

                    int currentDamageTaken = damageableMeta.getDamage();
                    int maxDurability = item.getType().getMaxDurability();
                    int damageToTake = 0;

                    switch(damage.getMode()){
                        case SIMPLE: // Damage the item for a random amount between min and max
                            damageToTake = (int) (damage.getMin() + (damage.getMax() - damage.getMin()) * rng.nextDouble());
                            break;
                        case PERCENTAGE:
                            // drop a percentage of items between min and max pct's
                            double percentage = damage.getMin() + (damage.getMax() - damage.getMin()) * rng.nextDouble();
                            damageToTake = (int) (maxDurability * (percentage / 100.0));
                            break;
                        case PERCENTAGE_REMAINING:
                            // drop a percentage of items between min and max pct's
                            double partialPercentage = damage.getMin() + (damage.getMax() - damage.getMin()) * rng.nextDouble();
                            damageToTake = (int) (currentDamageTaken * (partialPercentage / 100.0));
                            break;
                    }

                    if(damage.isUseEnchantments()){

                        if(item.getEnchantments().containsKey(Enchantment.DURABILITY)){

                            int level = item.getEnchantmentLevel(Enchantment.DURABILITY);

                            // Unbreaking X is often interpreted as unbreakable
                            if(level > 9){
                                continue;
                            }

                            // Reduce damage by 33% per unbreaking level
                            damageToTake = (int) (damageToTake * (1.0 - (0.33 * level)));
                        }

                    }

                    plugin.debug(ply, "Applying " + damageToTake + " damage to item in slot " + i + " (" + item.getType() + ")");


                    // Check wether the item would break
                    if( maxDurability - currentDamageTaken - damageToTake < 0 ){

                        // if the item should not break, we just set the damage to max durability
                        if(damage.isDontBreak() || item.getType() == Material.ELYTRA){
                            damageableMeta.setDamage(maxDurability);
                            item.setItemMeta(damageableMeta);
                            plugin.debug(ply, "Item in slot " + i + " (" + item.getType() + ") was saved from breaking.");
                        }else{
                            // Remove 1 from the item (some servers let you stack item with durability)
                            item.setAmount(item.getAmount() - 1);
                            damageableMeta.setDamage(0);
                            item.setItemMeta(meta);
                            ply.getWorld().playSound(ply.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8f, 0.8f);
                            plugin.debug(ply, "Item in slot " + i + " (" + item.getType() + ") broke.");
                        }

                    }else{
                        damageableMeta.setDamage(currentDamageTaken + damageToTake);
                        item.setItemMeta(meta);
                    }

                }
            }

        }

    }
}
