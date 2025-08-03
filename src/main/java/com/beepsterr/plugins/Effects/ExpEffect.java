package com.beepsterr.plugins.Effects;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Library.ConfigRule;
import com.beepsterr.plugins.Effects.Configs.ExpConfig;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ExpEffect extends Effect {

    ExpConfig expConfig;

    public ExpEffect(ConfigRule rule, ExpConfig expConfig) {
        super(rule);
        this.expConfig = expConfig;
    }

    @Override
    public void runRespawn(Player ply, PlayerRespawnEvent event) {

    }

    @Override
    public void runDeath(Player ply, PlayerDeathEvent event) {

        BetterKeepInventory plugin = BetterKeepInventory.getInstance();
        Random rng = plugin.rng;
        int playerExpLevel = ply.getLevel();
        int levelsToLose = 0;

        plugin.debug(ply," has " + playerExpLevel + " levels of experience.");


        switch(expConfig.getMode()){
            case SIMPLE: // Damage the item for a random amount between min and max
                levelsToLose = (int) (expConfig.getMin() + (expConfig.getMax() - expConfig.getMin()) * rng.nextDouble());
                break;
            case PERCENTAGE:
                // drop a percentage of items between min and max pct's
                double percentage = expConfig.getMin() + (expConfig.getMax() - expConfig.getMin()) * rng.nextDouble();
                levelsToLose = (int) (playerExpLevel * (percentage / 100.0));
                break;
            case ALL:
                levelsToLose = playerExpLevel;
        }

        plugin.debug(ply, "is losing " + levelsToLose + " levels of experience.");
        Map<String, String> replacements = new HashMap<>();
        replacements.put("amount", String.valueOf(levelsToLose));

        switch(expConfig.getHow()){
            case DELETE:
                ply.setLevel(playerExpLevel - levelsToLose);
                ply.setExp(0);
                break;
            case DROP:

                float expToDrop = 0;
                if(playerExpLevel <= levelsToLose){
                    expToDrop = getExpAtLevel(playerExpLevel) + ply.getExp();
                    ply.setLevel(0);
                    ply.setExp(0);
                }else{
                    expToDrop = getExpAtLevel(playerExpLevel) - getExpAtLevel(playerExpLevel - levelsToLose);
                    ply.setLevel(playerExpLevel - levelsToLose);
                    ply.setExp(0);
                }


                plugin.debug(ply, "dropping " + expToDrop + " experience points.");

                // create EXP orbs with the amount of expToDrop
                ExperienceOrb orb = ply.getWorld().spawn(ply.getLocation(), ExperienceOrb.class);
                orb.setExperience((int) Math.round(expToDrop));

        }

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
