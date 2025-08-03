package com.beepsterr.plugins.Library.Conditions.Vault;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Depends.Vault;
import com.beepsterr.plugins.Library.Conditions.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.regex.Pattern;

public class EconomyCondition implements Condition {

    private final double min_balance;

    public EconomyCondition(ConfigurationSection config) {
        this.min_balance = config.getDouble("economy.min_balance", 0.0);
    }

    @Override
    public boolean check(Player ply, PlayerDeathEvent deathEvent) {

        Vault vault = new Vault(BetterKeepInventory.getInstance());
        return vault.getPlayerBalance(ply) >= min_balance;

    }
}
