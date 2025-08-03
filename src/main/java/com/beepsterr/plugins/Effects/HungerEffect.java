package com.beepsterr.plugins.Effects;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Library.ConfigRule;
import com.beepsterr.plugins.Effects.Configs.HungerConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HungerEffect extends Effect {

    HungerConfig hungerConfig;
    public static Map<UUID, Integer> hungerMap = new HashMap<UUID, Integer>();


    public HungerEffect(ConfigRule rule, HungerConfig hungerConfig) {
        super(rule);
        this.hungerConfig = hungerConfig;
    }

    @Override
    public void runRespawn(Player ply, PlayerRespawnEvent event) {

        // restore the hunger level from the map
        Bukkit.getScheduler().runTaskLater(BetterKeepInventory.getInstance(), () -> {
            if(HungerEffect.hungerMap.containsKey(ply.getUniqueId())) {
                int hungerLevel = HungerEffect.hungerMap.get(ply.getUniqueId());
                ply.setFoodLevel(hungerLevel);
                HungerEffect.hungerMap.remove(ply.getUniqueId());
                BetterKeepInventory.getInstance().debug(ply, "set hunger level to " + hungerLevel + " after respawn.");
            }
        }, 5L);


    }

    @Override
    public void runDeath(Player ply, PlayerDeathEvent event) {

        // remove hungerConfig.amount from the player (clamped around hungerConfig.min)
        int currentHunger = ply.getFoodLevel();
        int hungerToLose = hungerConfig.getAmount();
        int newHunger = Math.max(currentHunger - hungerToLose, hungerConfig.getMin());

        HungerEffect.hungerMap.put(ply.getUniqueId(), newHunger);
        BetterKeepInventory.getInstance().debug(ply, "saving hunger level " + newHunger + " for respawn.");

    }

}
