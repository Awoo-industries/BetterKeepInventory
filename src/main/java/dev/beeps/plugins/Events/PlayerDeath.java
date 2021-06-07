package dev.beeps.plugins.Events;

import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerDeath implements Listener {

    BetterKeepInventory plugin;
    int[] armorSlots = new int[]{ 36,37,38,39 };

    public PlayerDeath(BetterKeepInventory main){
        plugin = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player ply = event.getEntity();

        boolean noArmorDamage = ply.hasPermission("betterkeepinventory.bypass.armor.damage");
        boolean noItemDamage = ply.hasPermission("betterkeepinventory.bypass.inventory.damage");
        boolean noArmorBreaking = ply.hasPermission("betterkeepinventory.bypass.armor.breaking");
        boolean noItemBreaking = ply.hasPermission("betterkeepinventory.bypass.inventory.breaking");

        // bypass permission
        if(ply.hasPermission("betterkeepinventory.bypass.all") ) return;

        // ignore if no keepInventory, config disabled or creative mode
        if(!event.getKeepInventory() || !plugin.config.getBoolean("enabled") || ply.getGameMode() == GameMode.CREATIVE){
            return;
        }

        Inventory inv = ply.getInventory();
        for (int size = 0; size<inv.getSize(); size++) {

            ItemStack item = inv.getItem(size);

            // empty can be skipped, optimize
            if(item == null) continue;

            ItemMeta meta = item.getItemMeta();
            Material type = item.getType();

            // air can be skipped.
            if(type == Material.AIR) continue;

            // slot permission checks
            if(BetterKeepInventory.contains(armorSlots, size) && noArmorDamage) continue;
            if(!BetterKeepInventory.contains(armorSlots, size) && noItemDamage) continue;

            if(meta instanceof Damageable){

                Damageable damageableMeta = (Damageable) meta;

                int min = plugin.config.getInt("min_damage_pct");
                int max = plugin.config.getInt("max_damage_pct");
                int roll = (int)Math.floor(Math.random()*(max-min+1)+min);

                int durabilityChop = type.getMaxDurability() / 100 * roll;

                if( !plugin.config.getBoolean("ignore_enchants") && item.getEnchantments().containsKey(Enchantment.DURABILITY)){

                    int level = item.getEnchantmentLevel(Enchantment.DURABILITY);
                    if(level == 10) return; // Unrbeaking X gets ignored.

                    durabilityChop = durabilityChop / (level+1);

                }

                damageableMeta.setDamage(damageableMeta.getDamage() + durabilityChop);
                item.setItemMeta(meta);

                if(type.getMaxDurability() - damageableMeta.getDamage() < 0){

                    if(
                            plugin.config.getBoolean("dont_break_items") ||
                                    BetterKeepInventory.contains(armorSlots, size) && noArmorBreaking ||
                                    !BetterKeepInventory.contains(armorSlots, size) && noItemBreaking
                    ){
                        damageableMeta.setDamage(type.getMaxDurability());
                        item.setItemMeta(meta);
                    }else{
                        inv.setItem(size, new ItemStack(Material.AIR));
                        ply.getWorld().playSound(ply.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 0.8f);
                    }

                }

            }

        }

    }


}
