package com.beepsterr.plugins.Library.Conditions.BetterKeepInventory;

import com.beepsterr.plugins.Library.Conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.regex.Pattern;

public class WorldCondition implements Condition {

    private final List<String> worlds;

    public WorldCondition(ConfigurationSection config) {
        this.worlds = config.getStringList("worlds");
    }

    @Override
    public boolean check(Player ply, PlayerDeathEvent deathEvent) {
        for (String world : worlds) {
            boolean isNegated = world.startsWith("!");
            String actualWorldPattern = isNegated ? world.substring(1) : world;

            // Convert wildcard (*) to regex pattern
            String regex = actualWorldPattern.replace("*", ".*");
            Pattern pattern = Pattern.compile(regex);
            boolean matches = pattern.matcher(ply.getWorld().getName()).matches();

            if (isNegated != matches) {
                return true;
            }
        }
        return false;
    }
}
