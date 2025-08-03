package com.beepsterr.plugins.Effects;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Library.ConfigRule;
import com.beepsterr.plugins.Library.DropItemsConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;
import java.util.Random;

public class DropItemEffect extends Effect {

    DropItemsConfig drop;

    public DropItemEffect(ConfigRule rule, DropItemsConfig drop){
        super(rule);
        this.drop = drop;
    }

    @Override
    public void runRespawn(Player ply, PlayerRespawnEvent event) {

    }

    @Override
    public void runDeath(Player ply, PlayerDeathEvent event) {

        BetterKeepInventory plugin = BetterKeepInventory.getInstance();
        Random rng = plugin.rng;

        // get items to drop
        List<Material> items = drop.getItems().getMaterials();

        // loop over the players inventory
        for (int i = 0; i < ply.getInventory().getSize(); i++) {
            // get the item in the slot
            var item = ply.getInventory().getItem(i);
            if (item != null && items.contains(item.getType())) {

                // We can simply drop it all if mode is set to ALL.
                if(drop.getMode() == DropItemsConfig.Mode.ALL){
                    ply.getWorld().dropItemNaturally(ply.getLocation(), item);
                    ply.getInventory().setItem(i, null);
                }

                // Now this is where it gets complicated kid
                // SIMPLE will drop a random amount of items between min and max
                // PERCENTAGE will dorp a percentage of items between min and max pct's

                int inventoryCount = item.getAmount();

                int removalCount = 0;
                switch(drop.getMode()){
                    case SIMPLE: // drop a random amount of items between min and max
                        removalCount = (int) (drop.getMin() + (drop.getMax() - drop.getMin()) * rng.nextDouble());
                        break;
                    case PERCENTAGE:
                        // drop a percentage of items between min and max pct's
                        double percentage = drop.getMin() + (drop.getMax() - drop.getMin()) * rng.nextDouble();
                        removalCount = (int) (inventoryCount * (percentage / 100.0));
                        break;
                }

                plugin.debug(ply, "DropItemEffect: Dropping " + removalCount + " items from slot " + i + " (" + item.getType() + ")");

                // make sure we don't drop more items than we have
                if(inventoryCount - removalCount < 0){
                    removalCount = inventoryCount;
                }

                // if removalCount is 0, we don't drop anything
                if(removalCount == 0){
                    continue;
                }

                // clone and drop item
                var itemClone = item.clone();
                itemClone.setAmount(removalCount);
                ply.getWorld().dropItemNaturally(ply.getLocation(), itemClone);

                // update the inventory with the new amount
                if(inventoryCount - removalCount == 0){
                    ply.getInventory().setItem(i, null); // if we have no items left, set to null
                }else{
                    item.setAmount(inventoryCount - removalCount);
                    ply.getInventory().setItem(i, item);
                }

            }
        }
    }
}
