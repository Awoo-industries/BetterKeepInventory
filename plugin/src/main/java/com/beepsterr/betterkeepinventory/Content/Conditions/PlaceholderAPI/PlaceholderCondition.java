package com.beepsterr.betterkeepinventory.Content.Conditions.PlaceholderAPI;

import com.beepsterr.betterkeepinventory.Library.PlaceholderItem;
import com.beepsterr.betterkeepinventory.api.Condition;
import com.beepsterr.betterkeepinventory.api.Exceptions.ConditionParseError;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderCondition implements Condition {

    private final List<PlaceholderItem> placeholderConditions;

    public PlaceholderCondition(ConfigurationSection config) throws ConditionParseError {
        this.placeholderConditions = new ArrayList<>();

        ConfigurationSection section = config.getConfigurationSection("placeholders");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection inner = section.getConfigurationSection(key);
                if (inner != null) {
                    this.placeholderConditions.add(new PlaceholderItem(inner));
                }
            }
        }
    }

    @Override
    public boolean check(Player ply, PlayerDeathEvent deathEvent, PlayerRespawnEvent respawnEvent) {
        for (PlaceholderItem item : this.placeholderConditions) {
            if (item.test(ply)) {
                return true;
            }
        }
        return false;
    }
}
