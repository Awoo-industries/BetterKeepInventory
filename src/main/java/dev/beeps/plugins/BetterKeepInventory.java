package dev.beeps.plugins;

/*
    COPYRIGHT (c) 2021 Jill "BeepSterr" Megens
    @author BeepSterr
 */

import org.bstats.bukkit.Metrics;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class BetterKeepInventory extends JavaPlugin implements Listener {

    FileConfiguration config = getConfig();

    int[] armorSlots = new int[]{ 36,37,38,39 };

    @Override
    public void onEnable() {

        config.addDefault("enabled", true);
        config.addDefault("ignore_enchants", false);
        config.addDefault("dont_break_items", false);
        config.addDefault("min_damage_pct", 25);
        config.addDefault("max_damage_pct", 35);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 11596);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
        if(!event.getKeepInventory() || !config.getBoolean("enabled") || ply.getGameMode() == GameMode.CREATIVE){
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

            ply.getServer().broadcastMessage( String.format("%d = %s", size, type) );

            // slot permission checks
            if(contains(armorSlots, size) && noArmorDamage) continue;
            if(!contains(armorSlots, size) && noItemDamage) continue;

            if(meta instanceof Damageable){

                Damageable damageableMeta = (Damageable) meta;

                int min = config.getInt("min_damage_pct");
                int max = config.getInt("max_damage_pct");
                int roll = (int)Math.floor(Math.random()*(max-min+1)+min);

                int durabilityChop = type.getMaxDurability() / 100 * roll;

                if( !config.getBoolean("ignore_enchants") && item.getEnchantments().containsKey(Enchantment.DURABILITY)){

                    int level = item.getEnchantmentLevel(Enchantment.DURABILITY);
                    if(level == 10) return; // Unrbeaking X gets ignored.

                    durabilityChop = durabilityChop / (level+1);

                }

                damageableMeta.setDamage(damageableMeta.getDamage() + durabilityChop);
                item.setItemMeta(meta);

                if(type.getMaxDurability() - damageableMeta.getDamage() < 0){

                    if(
                            config.getBoolean("dont_break_items") ||
                            contains(armorSlots, size) && noArmorBreaking ||
                            !contains(armorSlots, size) && noItemBreaking
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

    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }
    
}
