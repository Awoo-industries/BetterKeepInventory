package com.beepsterr.plugin_api_consumer;

import com.beepsterr.betterkeepinventory.api.BetterKeepInventoryAPI;
import com.beepsterr.betterkeepinventory.api.Condition;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BKITestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        BetterKeepInventoryAPI api = Bukkit.getServicesManager().load(BetterKeepInventoryAPI.class);
        // don't forget null checks!
        api.conditionRegistry().register(this, "worlds", AlwaysTrueCondition::new);
        getLogger().info("✅ Custom test condition registered!");
    }

    public static class AlwaysTrueCondition implements Condition {

        public AlwaysTrueCondition(ConfigurationSection section) {
            // no config needed in this example
            // but here you can use standard bukkit config API to read your conditions values
        }

        @Override
        public boolean check(Player player, PlayerDeathEvent deathEvent, PlayerRespawnEvent respawnEvent) {
            return false;
        }
    }
}
