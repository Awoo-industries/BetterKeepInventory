package com.beepsterr.betterkeepinventory.Content.Conditions;

import com.beepsterr.betterkeepinventory.api.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;
import java.util.regex.Pattern;

public class WorldsCondition implements Condition {

    private final List<String> worlds;

    public WorldsCondition(ConfigurationSection config) {
        this.worlds = config.getStringList("nodes");
    }

    @Override
    public boolean check(Player ply, PlayerDeathEvent deathEvent, PlayerRespawnEvent respawnEvent) {
        for (String world : worlds) {
            boolean isNegated = world.startsWith("!");
            String actual = isNegated ? world.substring(1) : world;

            String regex = actual.replace("*", ".*");
            Pattern pattern = Pattern.compile(regex);
            boolean matches = pattern.matcher(ply.getWorld().getName()).matches();

            if (isNegated != matches) {
                return true;
            }
        }
        return false;
    }
}
