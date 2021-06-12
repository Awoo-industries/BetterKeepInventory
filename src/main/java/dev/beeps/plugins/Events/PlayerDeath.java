package dev.beeps.plugins.Events;

import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerDeath implements Listener {

    BetterKeepInventory plugin;
    int[] armorSlots = new int[]{ 36,37,38,39 };

    Map<UUID, Integer> foodLevelMap = new HashMap<UUID, Integer>();

    public PlayerDeath(BetterKeepInventory main){
        plugin = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        if(!plugin.config.getBoolean("enabled", false)){
            return;
        }

        Player ply = event.getEntity();

        foodLevelMap.put(ply.getUniqueId(), ply.getFoodLevel());

        handleItems(ply, event);
        handleExp(ply, event);

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player ply = event.getPlayer();

        if(!plugin.config.getBoolean("keep_hunger_level", false)) return;
        if(ply.hasPermission("betterkeepinventory.bypass.hunger")) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ply.setFoodLevel(foodLevelMap.get(ply.getUniqueId()));
            foodLevelMap.remove(ply.getUniqueId());

            if(ply.getFoodLevel() < plugin.config.getInt("keep_hunger_level_min")){
                ply.setFoodLevel(plugin.config.getInt("keep_hunger_level_min"));
            }

        }, 20L);

    }

    public void handleExp(Player ply, PlayerDeathEvent event){

        if(!plugin.config.getBoolean("enable_xp_loss")) return;

        if(!ply.hasPermission("betterkeepinventory.bypass.xp.level")){

            double min = plugin.config.getDouble("min_xp_loss_pct");
            double max = plugin.config.getDouble("max_xp_loss_pct");
            double roll = Math.floor(Math.random()*(max-min+1)+min);
            int loss = (int) Math.floor( ply.getLevel() - (ply.getLevel() / 100f * roll));

            ply.setLevel(loss);

        }

        if(
            plugin.config.getBoolean("xp_reset_current_level_progress", false)
            && !ply.hasPermission("betterkeepinventory.bypass.xp")
        ){
            ply.setExp(0f);
        }

    }

    public void handleItems(Player ply, PlayerDeathEvent event){

        boolean noArmorDamage = ply.hasPermission("betterkeepinventory.bypass.armor.damage");
        boolean noItemDamage = ply.hasPermission("betterkeepinventory.bypass.inventory.damage");
        boolean noArmorBreaking = ply.hasPermission("betterkeepinventory.bypass.armor.breaking");
        boolean noItemBreaking = ply.hasPermission("betterkeepinventory.bypass.inventory.breaking");

        // bypass permission
        if(ply.hasPermission("betterkeepinventory.bypass.all") ) return;

        // ignore if no keepInventory, config disabled or creative mode
        if(!event.getKeepInventory() || !plugin.config.getBoolean("enable_item_damage", false) || ply.getGameMode() == GameMode.CREATIVE){
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

                double min = plugin.config.getDouble("min_damage_pct");
                double max = plugin.config.getDouble("max_damage_pct");
                double roll = Math.floor(Math.random()*(max-min+1)+min);

                int durabilityChop = (int) ((int)type.getMaxDurability() / 100 * roll);

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
