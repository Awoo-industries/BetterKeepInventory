package com.beepsterr.plugins.Effects;

import com.beepsterr.plugins.Library.ConfigRule;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

abstract class Effect {

    ConfigRule rule;

    public Effect(ConfigRule rule){
        this.rule = rule;
    }

    abstract public void runRespawn(Player ply, PlayerRespawnEvent event);
    abstract public void runDeath(Player ply, PlayerDeathEvent event);

}
