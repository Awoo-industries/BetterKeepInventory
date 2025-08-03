package com.beepsterr.plugins.Effects;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Depends.Vault;
import com.beepsterr.plugins.Effects.Configs.EconomyConfig;
import com.beepsterr.plugins.Library.ConfigRule;
import com.beepsterr.plugins.Effects.Configs.ExpConfig;
import com.beepsterr.plugins.Library.Types.MaterialType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EconomyEffect extends Effect {

    EconomyConfig economyConfig;

    public EconomyEffect(ConfigRule rule, EconomyConfig expConfig) {
        super(rule);
        this.economyConfig = expConfig;
    }

    @Override
    public void runRespawn(Player ply, PlayerRespawnEvent event) {

    }

    @Override
    public void runDeath(Player ply, PlayerDeathEvent event) {

        Vault vault = new Vault(BetterKeepInventory.getInstance());
        BetterKeepInventory plugin = BetterKeepInventory.getInstance();
        Random rng = plugin.rng;

        double PlayerBalance = vault.getPlayerBalance(ply);
        double BalanceLoss = 0;
        switch(economyConfig.getMode()){
            case SIMPLE: // Damage the item for a random amount between min and max
                BalanceLoss = economyConfig.getMin() + (economyConfig.getMax() - economyConfig.getMin()) * rng.nextDouble();
                break;
            case PERCENTAGE:
                // drop a percentage of items between min and max pct's
                double percentage = economyConfig.getMin() + (economyConfig.getMax() - economyConfig.getMin()) * rng.nextDouble();
                BalanceLoss = PlayerBalance * (percentage / 100.0);
                break;
        }

        if(PlayerBalance < BalanceLoss && !economyConfig.isNegativeBalanceAllowed()){
            BalanceLoss = PlayerBalance; // Don't let the player go into debt
        }

        vault.takeMoney(ply, BalanceLoss);

        // send message
        Map<String, String> replacements = new HashMap<>();
        replacements.put("amount", vault.format(BalanceLoss));
        plugin.config.sendMessage(ply, "effects.economy", replacements);


    }

    // Calculate amount of EXP needed to level up
    public static int getExpToLevelUp(int level){
        if(level <= 15){
            return 2*level+7;
        } else if(level <= 30){
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    // Calculate total experience up to a level
    public static int getExpAtLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        } else if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }


}
