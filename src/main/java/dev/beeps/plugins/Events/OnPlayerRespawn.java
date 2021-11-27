package dev.beeps.plugins.Events;

import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.event.Listener;

public class OnPlayerRespawn implements Listener {

    BetterKeepInventory plugin;

    public OnPlayerRespawn(BetterKeepInventory main){
        plugin = main;
    }
}
