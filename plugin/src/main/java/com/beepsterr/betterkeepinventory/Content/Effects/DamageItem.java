package com.beepsterr.betterkeepinventory.Content.Effects;

import com.beepsterr.betterkeepinventory.BetterKeepInventory;
import com.beepsterr.betterkeepinventory.Library.Types.MaterialType;
import com.beepsterr.betterkeepinventory.Library.Types.SlotType;
import com.beepsterr.betterkeepinventory.api.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DamageItem implements Effect {

    public enum Mode {
        PERCENTAGE, PERCENTAGE_REMAINING, SIMPLE
    }

    private final Mode mode;
    private final float min;
    private final float max;
    private final boolean useEnchantments;
    private final boolean dontBreak;
    private final SlotType slots;
    private final MaterialType items;

    public DamageItem(ConfigurationSection config) {
        this.mode = Mode.valueOf(config.getString("mode", "PERCENTAGE").toUpperCase());
        this.min = (float) config.getDouble("min", 0.0);
        this.max = (float) config.getDouble("max", 0.0);
        this.useEnchantments = config.getBoolean("use_enchantments", false);
        this.dontBreak = config.getBoolean("dont_break", false);
        this.slots = new SlotType(config.getStringList("slots"));
        this.items = new MaterialType(config.getStringList("items"));

    }

    @Override
    public void onRespawn(Player player, PlayerRespawnEvent event) {
        // This effect doesn't do anything on respawn (yet)
    }

    @Override
    public void onDeath(Player ply, PlayerDeathEvent event) {
        BetterKeepInventory plugin = BetterKeepInventory.getInstance();
        Random rng = plugin.rng;

        List<Integer> slots = this.slots.getSlotIds();
        List<Material> items = this.items.getMaterials();

        for (int i = 0; i < ply.getInventory().getSize(); i++) {
            var item = ply.getInventory().getItem(i);
            if (item == null || !items.contains(item.getType())) continue;

            ItemMeta meta = item.getItemMeta();
            if (!(meta instanceof Damageable damageableMeta)) continue;
            if (!slots.contains(i)) continue;

            int currentDamageTaken = damageableMeta.getDamage();
            int maxDurability = item.getType().getMaxDurability();
            int damageToTake = calculateDamage(rng, currentDamageTaken, maxDurability);

            if (damageToTake < 0) continue;

            damageToTake = applyUnbreaking(item, damageToTake);
            Map<String, String> replacements = new HashMap<>();
            replacements.put("amount", String.valueOf(damageToTake));
            replacements.put("item", MaterialType.GetName(item));

            if (maxDurability - currentDamageTaken - damageToTake < 0) {
                if (dontBreak || item.getType() == Material.ELYTRA) { // elytra is special, it doesn't break
                    damageableMeta.setDamage(maxDurability);
                    item.setItemMeta(damageableMeta);
                    plugin.debug(ply, "Item saved from breaking: " + item.getType());
                    plugin.config.sendMessage(ply, "effects.damage", replacements);
                } else {
                    item.setAmount(item.getAmount() - 1);
                    damageableMeta.setDamage(0);
                    item.setItemMeta(meta);
                    ply.getWorld().playSound(ply.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.8f, 0.8f);
                    plugin.debug(ply, "Item broke: " + item.getType());
                    plugin.config.sendMessage(ply, "effects.damage_break", replacements);
                }
            } else {
                damageableMeta.setDamage(currentDamageTaken + damageToTake);
                item.setItemMeta(meta);
                plugin.config.sendMessage(ply, "effects.damage", replacements);
            }
        }
    }

    private int calculateDamage(Random rng, int currentDamageTaken, int maxDurability) {
        return switch (mode) {
            case SIMPLE -> (int) (min + (max - min) * rng.nextDouble());
            case PERCENTAGE -> (int) (maxDurability * ((min + (max - min) * rng.nextDouble()) / 100.0));
            case PERCENTAGE_REMAINING -> (int) (currentDamageTaken * ((min + (max - min) * rng.nextDouble()) / 100.0));
        };
    }

    private int applyUnbreaking(org.bukkit.inventory.ItemStack item, int damageToTake) {
        if (!useEnchantments) return damageToTake;
        if (!item.getEnchantments().containsKey(Enchantment.DURABILITY)) return damageToTake;

        int level = item.getEnchantmentLevel(Enchantment.DURABILITY);
        if (level > 9) return 0; // interpreted as unbreakable
        return (int) (damageToTake * (1.0 - (0.33 * level)));
    }
}
