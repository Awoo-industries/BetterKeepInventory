package com.beepsterr.betterkeepinventory.Content.Effects;

import com.beepsterr.betterkeepinventory.BetterKeepInventory;
import com.beepsterr.betterkeepinventory.api.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Exp implements Effect {

    public enum Mode {
        SIMPLE, PERCENTAGE, ALL
    }

    public enum How {
        DELETE, DROP
    }

    private final Mode mode;
    private final How how;
    private final float min;
    private final float max;

    public Exp(ConfigurationSection config) {
        this.mode = Mode.valueOf(config.getString("mode", "PERCENTAGE").toUpperCase());
        this.how = How.valueOf(config.getString("how", "DROP").toUpperCase());
        this.min = (float) config.getDouble("min", 0.0);
        this.max = (float) config.getDouble("max", 0.0);
    }

    @Override
    public void onRespawn(Player ply, PlayerRespawnEvent event) {
        // Nothing on respawn
    }

    @Override
    public void onDeath(Player ply, PlayerDeathEvent event) {
        BetterKeepInventory plugin = BetterKeepInventory.getInstance();
        Random rng = plugin.rng;

        int playerExpLevel = ply.getLevel();
        int levelsToLose = switch (mode) {
            case SIMPLE -> (int) (min + (max - min) * rng.nextDouble());
            case PERCENTAGE -> (int) (playerExpLevel * ((min + (max - min) * rng.nextDouble()) / 100.0));
            case ALL -> playerExpLevel;
        };

        plugin.debug(ply, "has " + playerExpLevel + " levels of experience.");
        plugin.debug(ply, "is losing " + levelsToLose + " levels of experience.");

        Map<String, String> replacements = new HashMap<>();
        replacements.put("amount", String.valueOf(levelsToLose));

        switch (how) {
            case DELETE -> {
                ply.setLevel(playerExpLevel - levelsToLose);
                ply.setExp(0);
            }
            case DROP -> {
                float expToDrop;
                if (playerExpLevel <= levelsToLose) {
                    expToDrop = getExpAtLevel(playerExpLevel) + ply.getExp();
                    ply.setLevel(0);
                    ply.setExp(0);
                } else {
                    expToDrop = getExpAtLevel(playerExpLevel) - getExpAtLevel(playerExpLevel - levelsToLose);
                    ply.setLevel(playerExpLevel - levelsToLose);
                    ply.setExp(0);
                }

                plugin.debug(ply, "dropping " + expToDrop + " experience points.");

                ExperienceOrb orb = ply.getWorld().spawn(ply.getLocation(), ExperienceOrb.class);
                orb.setExperience((int) Math.round(expToDrop));
            }
        }
    }

    private int getExpAtLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }
}
