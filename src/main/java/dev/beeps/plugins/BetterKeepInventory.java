package dev.beeps.plugins;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterKeepInventory extends JavaPlugin implements Listener {

    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {

        config.addDefault("enabled", true);
        config.addDefault("min_damage", 4);
        config.addDefault("max_damage", 8);
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
        getServer().broadcastMessage(String.format("Player %s has died.", ply.getUniqueId()));


        // ignore if no keepInventory is on
        if(!event.getKeepInventory() || !config.getBoolean("enabled")){
            return;
        }

        Inventory inv = ply.getInventory();
        for (int size = 0; size<inv.getSize(); size++) {

            ItemStack item = inv.getItem(size);
            if(item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if(meta instanceof Damageable){
                Material type = item.getType();

                int min = config.getInt("min_damage");
                int max = config.getInt("max_damage");

                int durabilityChop = type.getMaxDurability() / (int)Math.floor(Math.random()*(max-min+1)+min);;

                ((Damageable) meta).setDamage(((Damageable) meta).getDamage() + durabilityChop);

                item.setItemMeta(meta);

                if(type.getMaxDurability() - ((Damageable) meta).getDamage() < 0){
                    inv.setItem(size, new ItemStack(Material.AIR));
                    ply.getWorld().playSound(ply.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 0.8f);
                }

            }

        }

    }
    
}
