package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Conditions {

    private List<String> permissions;
    private List<String> worlds;
    private List<String> townyTowns;
    private List<String> townyNations;
    private List<String> griefPreventionClaims;
    private List<PlaceholderCondition> placeholderConditions;

    public Conditions(ConfigurationSection config) throws ConfigurationException {

        BetterKeepInventory plugin = BetterKeepInventory.getInstance();

        this.permissions = config.getStringList( "permissions");
        this.worlds = config.getStringList("worlds");

        // Ensure Towny is loaded if Towny conditions are present
        if(config.contains("towny_towns") && !plugin.checkDependency("Towny")){
            throw new ConfigurationException(config.getCurrentPath() + ".towny_towns", "The condition requires Towny, but it is not loaded!");
        }

        if(config.contains("towny_nations") && !plugin.checkDependency("Towny")){
            throw new ConfigurationException(config.getCurrentPath() + ".towny_nations", "The condition requires Towny, but it is not loaded!");
        }

        this.townyTowns = config.getStringList("towny_towns");
        this.townyNations = config.getStringList("towny_nations");

        // Ensure GriefPrevention is loaded if GriefPrevention conditions are present
        if(config.contains("griefprevention_claims") && !plugin.checkDependency("GriefPrevention")){
            throw new ConfigurationException(config.getCurrentPath() + ".griefprevention_claims", "The condition requires GriefPrevention, but it is not loaded!");
        }
        this.griefPreventionClaims = config.getStringList( "griefprevention_claims");

        // Ensure PlaceholderAPI is loaded if Placeholder conditions are present
        if(config.contains("placeholders") && !plugin.checkDependency("PlaceholderAPI")){
            throw new ConfigurationException(config.getCurrentPath() + ".placeholders", "The condition requires PlaceholderAPI, but it is not loaded!");
        }

        this.placeholderConditions = new ArrayList<>();
        ConfigurationSection placeholderConditions = config.getConfigurationSection("placeholders");
        if (placeholderConditions != null) {
            for (String placeholderKey : placeholderConditions.getKeys(false)) {
                ConfigurationSection placeholderConditionSection = placeholderConditions.getConfigurationSection(placeholderKey);
                if (placeholderConditionSection != null) {
                    this.placeholderConditions.add(new PlaceholderCondition(placeholderConditionSection));
                }
            }
        }
    }

    public boolean check(Player ply, PlayerDeathEvent event) {

        boolean permissionMatched = true;
        boolean worldMatched = true;
        boolean placeholderMatched = true;

        if(permissions != null && !permissions.isEmpty()){
            permissionMatched = false;

            // Check permissions (prefixed with ! for negation)
            for (String perm : permissions) {
                boolean isNegated = perm.startsWith("!");
                String actualPerm = isNegated ? perm.substring(1) : perm;

                if (isNegated != ply.hasPermission(actualPerm)) {
                    permissionMatched = true;
                    break; // No need to check further once a match is found
                }
            }

        }

        if(worlds != null && !worlds.isEmpty()){
            worldMatched = false;

            // Check worlds (prefixed with ! for negation and supports regex)
            for (String world : worlds) {
                boolean isNegated = world.startsWith("!");
                String actualWorldPattern = isNegated ? world.substring(1) : world;

                // Convert wildcard (*) to regex pattern
                String regex = actualWorldPattern.replace("*", ".*");
                Pattern pattern = Pattern.compile(regex);
                boolean matches = pattern.matcher(ply.getWorld().getName()).matches();

                if (isNegated != matches) {
                    worldMatched = true;
                    break; // No need to check further once a match is found
                }
            }
        }

        if(this.placeholderConditions != null && !this.placeholderConditions.isEmpty()){
            placeholderMatched = false;

            for(PlaceholderCondition condition : this.placeholderConditions){
                BetterKeepInventory.getInstance().getLogger().info("Placeholder Condition...");
                if(condition.test(ply)){
                    placeholderMatched = true;
                    break;
                }

            }
        }

        // Return true only if both conditions are satisfied
        return permissionMatched && worldMatched && placeholderMatched;
    }
}

