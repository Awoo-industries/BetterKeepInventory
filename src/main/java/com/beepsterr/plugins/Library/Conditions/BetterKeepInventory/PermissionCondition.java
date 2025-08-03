package com.beepsterr.plugins.Library.Conditions.BetterKeepInventory;

import com.beepsterr.plugins.Library.Conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.regex.Pattern;

public class PermissionCondition implements Condition {

    private final List<String> permissions;

    public PermissionCondition(ConfigurationSection config) {
        this.permissions = config.getStringList("permissions");
    }

    @Override
    public boolean check(Player ply, PlayerDeathEvent deathEvent) {
        // Check permissions (prefixed with ! for negation)
        for (String perm : permissions) {
            boolean isNegated = perm.startsWith("!");
            String actualPerm = isNegated ? perm.substring(1) : perm;

            if (isNegated != ply.hasPermission(actualPerm)) {
                return true;
            }
        }
        return false;
    }
}
