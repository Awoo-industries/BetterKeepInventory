package com.beepsterr.betterkeepinventory.Content.Effects;

import com.beepsterr.betterkeepinventory.BetterKeepInventory;
import com.beepsterr.betterkeepinventory.api.Types.MaterialType;
import com.beepsterr.betterkeepinventory.api.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DropItemEffect implements Effect {

    public enum Mode {
        SIMPLE, PERCENTAGE, ALL
    }

    private final Mode mode;
    private final float min;
    private final float max;
    private final MaterialType items;

    public DropItemEffect(ConfigurationSection config) {
        this.mode = Mode.valueOf(config.getString("mode", "SIMPLE").toUpperCase());
        this.min = (float) config.getDouble("min", 0.0);
        this.max = (float) config.getDouble("max", 0.0);
        this.items = new MaterialType(config.getStringList("items"));
    }

    @Override
    public void onRespawn(Player ply, PlayerRespawnEvent event) {
        // Nothing on respawn
    }

    @Override
    public void onDeath(Player ply, PlayerDeathEvent event) {
        BetterKeepInventory plugin = BetterKeepInventory.getInstance();
        Random rng = plugin.rng;

        List<Material> dropItems = items.getMaterials();

        for (int i = 0; i < ply.getInventory().getSize(); i++) {
            var item = ply.getInventory().getItem(i);
            if (item == null || !dropItems.contains(item.getType())) continue;

            // Drop all
            if (mode == Mode.ALL) {
                ply.getWorld().dropItemNaturally(ply.getLocation(), item);
                ply.getInventory().setItem(i, null);
                continue;
            }

            int inventoryCount = item.getAmount();
            int removalCount = 0;

            switch (mode) {
                case SIMPLE -> removalCount = (int) (min + (max - min) * rng.nextDouble());
                case PERCENTAGE -> {
                    double percentage = min + (max - min) * rng.nextDouble();
                    removalCount = (int) (inventoryCount * (percentage / 100.0));
                }
            }

            if (removalCount <= 0) continue;

            Map<String, String> replacements = new HashMap<>();
            replacements.put("amount", String.valueOf(removalCount));
            replacements.put("item", MaterialType.GetName(item));
            plugin.config.sendMessage(ply, "effects.drop", replacements);

            plugin.debug(ply, "DropItemEffect: Dropping " + removalCount + " items from slot " + i + " (" + item.getType() + ")");

            if (inventoryCount - removalCount < 0) {
                removalCount = inventoryCount;
            }

            if (removalCount == 0) continue;

            var itemClone = item.clone();
            itemClone.setAmount(removalCount);
            ply.getWorld().dropItemNaturally(ply.getLocation(), itemClone);

            if (inventoryCount - removalCount == 0) {
                ply.getInventory().setItem(i, null);
            } else {
                item.setAmount(inventoryCount - removalCount);
                ply.getInventory().setItem(i, item);
            }
        }
    }
}
