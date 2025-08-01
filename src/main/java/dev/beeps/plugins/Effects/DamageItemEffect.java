package dev.beeps.plugins.Effects;

import dev.beeps.plugins.Library.ConfigRule;
import dev.beeps.plugins.Library.DamageItems;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DamageItemEffect extends Effect {

    DamageItems damage;

    public DamageItemEffect(ConfigRule rule, DamageItems damage){
        super(rule);
        this.damage = damage;
    }

    @Override
    public void runRespawn(Player ply, PlayerRespawnEvent event) {

    }

    @Override
    public void runDeath(Player ply, PlayerDeathEvent event) {
        // send to chat
        ply.sendMessage("You have died!");
        ply.sendMessage("You have lost " + damage.getMax() + " durability from your items!");
    }
}
