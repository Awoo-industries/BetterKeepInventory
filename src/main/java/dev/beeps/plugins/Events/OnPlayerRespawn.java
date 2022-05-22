package dev.beeps.plugins.Events;

import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnPlayerRespawn implements Listener {

    BetterKeepInventory plugin;

    public OnPlayerRespawn(BetterKeepInventory main){
        plugin = main;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player ply = event.getPlayer();
        doHunger(event, ply);
        doPotions(event, ply);

    }

    private void doHunger(PlayerRespawnEvent evt, Player ply){

        if(ply.hasPermission("betterkeepinventory.bypass.hunger")) return;
        if(plugin.config.GetOverrideForMode("HUNGER", ply)) return;

        switch(plugin.config.getHungerMode("hunger.mode")){
            case NONE:
                return;
            case KEEP:

                Bukkit.getScheduler().runTaskLater(plugin, () -> {

                    if(plugin.hungerMap.containsKey(ply.getUniqueId())){
                        ply.setFoodLevel(plugin.hungerMap.get(ply.getUniqueId()));
                        plugin.hungerMap.remove(ply.getUniqueId());

                        if(ply.getFoodLevel() < plugin.config.getInt("hunger.min")){
                            ply.setFoodLevel(plugin.config.getInt("hunger.min"));
                        }

                        if(ply.getFoodLevel() > plugin.config.getInt("hunger.max")){
                            ply.setFoodLevel(plugin.config.getInt("hunger.max"));
                        }
                    }

                }, 20L);

                break;
        }
    }

    private void doPotions(PlayerRespawnEvent evt, Player ply){

        if(plugin.config.GetOverrideForMode("POTIONS", ply)) return;

        if(!ply.hasPermission("betterkeepinventory.bypass.potions") ) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                if(plugin.potionMap.containsKey(ply.getUniqueId())){
                    ply.addPotionEffects(plugin.potionMap.get(ply.getUniqueId()));
                    plugin.potionMap.remove(ply.getUniqueId());
                }

            }, 20L);
        }

    }
}
