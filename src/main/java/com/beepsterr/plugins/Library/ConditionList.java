package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import com.beepsterr.plugins.Library.Conditions.BetterKeepInventory.PermissionCondition;
import com.beepsterr.plugins.Library.Conditions.BetterKeepInventory.WorldCondition;
import com.beepsterr.plugins.Library.Conditions.Condition;
import com.beepsterr.plugins.Library.Conditions.PlaceholderAPI.PlaceholderCondition;
import com.beepsterr.plugins.Library.Conditions.PlaceholderAPI.PlaceholderConditionItem;
import com.beepsterr.plugins.Library.Conditions.Vault.EconomyCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConditionList {

    private final List<Condition> conditions = new ArrayList<>();

    public ConditionList(ConfigurationSection config) throws ConfigurationException {

        BetterKeepInventory plugin = BetterKeepInventory.getInstance();

        if(!config.getStringList("permissions").isEmpty()){
            conditions.add(new PermissionCondition(config));
        }

        if(!config.getStringList("worlds").isEmpty()){
            conditions.add(new WorldCondition(config));
        }

        if(!config.getStringList("placeholders").isEmpty() && plugin.checkDependency("PlaceholderAPI")) {
            conditions.add(new PlaceholderCondition(config));
        }

        if(config.isConfigurationSection("economy") && plugin.checkDependency("Vault")) {
            conditions.add(new EconomyCondition(config));
        }

    }

    public boolean check(Player ply, PlayerDeathEvent deathEvent) {
        for(Condition condition : conditions) {
            if (!condition.check(ply, deathEvent)) {
                BetterKeepInventory.getInstance().debug(ply, "Condition failed: " + condition.getClass().getSimpleName());
                return false;
            }
        }
        BetterKeepInventory.getInstance().debug(ply, "All conditions passed.");
        return true;
    }
}

