package com.beepsterr.betterkeepinventory.Events;

import com.beepsterr.betterkeepinventory.BetterKeepInventory;
import com.beepsterr.betterkeepinventory.Library.ConfigRule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnPlayerDeath  implements Listener {

    BetterKeepInventory plugin;


    public OnPlayerDeath(BetterKeepInventory main){
        plugin = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player ply = event.getEntity();

        // Set base level of keepinv
        switch(plugin.config.getDefaultBehavior()){
            case KEEP:
                // these are needed to prevent dupes!!
                event.getDrops().clear();
                event.setDroppedExp(0);

                event.setKeepLevel(true);
                event.setKeepInventory(true);
                break;
            case DROP:
                event.setKeepLevel(false);
                event.setKeepInventory(false);
                break;
            // No case needed for INHERIT, as it will default to the world/other plugins behavior
        }

        BetterKeepInventory.instance.metrics.deathsProcessed +=1;

        // Time to process the top level rules
        for(ConfigRule rule : plugin.config.getRules()){
            rule.trigger(ply, event, null);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        // Time to process the top level rules
        for(ConfigRule rule : plugin.config.getRules()){
            rule.trigger(event.getPlayer(), null, event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player ply = event.getPlayer();
        if(ply.hasPermission("betterkeepinventory.version.notify")){

            // Delay the message to hopefully catch more attention
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    // If the version checker is enabled, and the found version is newer...
                    if(
                            plugin.versionChecker != null
                         && plugin.versionChecker.IsUpdateAvailable()){
                        // Send a message to the player
                        ply.sendMessage(ChatColor.YELLOW + "A new version of BetterKeepInventory is available!");
                        ply.sendMessage( ChatColor.GREEN + plugin.versionChecker.foundVersion.toString() + ChatColor.YELLOW + " (Installed: " + plugin.version.toString() + ")");

                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "A new version of BetterKeepInventory is available!");
                        Bukkit.getConsoleSender().sendMessage( ChatColor.GREEN + plugin.versionChecker.foundVersion.toString() + ChatColor.YELLOW + " (Installed: " + plugin.version.toString() + ")");
                    }
                }
            }, 20L);
        }

    }


}
