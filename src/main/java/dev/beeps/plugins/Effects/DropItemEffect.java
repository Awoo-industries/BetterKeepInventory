package dev.beeps.plugins.Effects;

import dev.beeps.plugins.Library.ConfigRule;
import dev.beeps.plugins.Library.DropItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class DropItemEffect extends Effect {

    DropItems drop;

    public DropItemEffect(ConfigRule rule, DropItems drop){
        super(rule);
        this.drop = drop;
    }

    @Override
    public void runRespawn(Player ply, PlayerRespawnEvent event) {

    }

    @Override
    public void runDeath(Player ply, PlayerDeathEvent event) {

        // get items to drop
        List<Material> items = drop.getItems().getMaterials();

        // loop over the players inventory
        for (int i = 0; i < ply.getInventory().getSize(); i++) {
            // get the item in the slot
            var item = ply.getInventory().getItem(i);
            if (item != null && items.contains(item.getType())) {

                // drop the item
                ply.getWorld().dropItemNaturally(ply.getLocation(), item);
                ply.getInventory().setItem(i, null);

            }
        }
    }
}
