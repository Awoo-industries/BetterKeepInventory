package com.beepsterr.plugins.Events;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Library.ConfigRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

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

        // Time to process the top level rules
        for(ConfigRule rule : plugin.config.getRules()){
            rule.trigger(ply, event);
        }

    }
}
