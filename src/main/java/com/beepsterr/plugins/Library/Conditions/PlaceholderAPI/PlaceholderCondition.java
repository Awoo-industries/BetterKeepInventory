package com.beepsterr.plugins.Library.Conditions.PlaceholderAPI;

import com.beepsterr.plugins.Exceptions.ConfigurationException;
import com.beepsterr.plugins.Library.Conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PlaceholderCondition implements Condition {

    private final List<PlaceholderConditionItem> placeholderConditions;

    public PlaceholderCondition(ConfigurationSection config) throws ConfigurationException {
        this.placeholderConditions = new ArrayList<>();
        ConfigurationSection placeholderConditions = config.getConfigurationSection("placeholders");
        if (placeholderConditions != null) {
            for (String placeholderKey : placeholderConditions.getKeys(false)) {
                ConfigurationSection placeholderConditionSection = placeholderConditions.getConfigurationSection(placeholderKey);
                if (placeholderConditionSection != null) {
                    this.placeholderConditions.add(new PlaceholderConditionItem(placeholderConditionSection));
                }
            }
        }
    }

    @Override
    public boolean check(Player ply, PlayerDeathEvent deathEvent) {
        for(PlaceholderConditionItem condition : this.placeholderConditions){
            if(condition.test(ply)){
                return true;
            }
        }
        return false;
    }
}
