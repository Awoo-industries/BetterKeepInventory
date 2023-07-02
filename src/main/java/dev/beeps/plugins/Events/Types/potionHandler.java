package dev.beeps.plugins.Events.Types;

import dev.beeps.plugins.BetterKeepInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class potionHandler {

    public potionHandler(BetterKeepInventory plugin, Player ply){

        // get config
        int reduce_potency = plugin.config.getInt("potions.reduce_potency");
        int max_duration = plugin.config.getInt("potions.max_duration") * 20;
        int duration_penalty = plugin.config.getInt("potions.duration_penalty") * 20;
        boolean change_duration = plugin.config.getBoolean("potions.max_duration", true);

        // get effect stuff
        List<PotionEffectType> kept_effects = plugin.config.getEffectList("potions.kept_effects");
        Collection<PotionEffect> ply_effects = ply.getActivePotionEffects();
        ArrayList<PotionEffect> reapplied_effect_list = new ArrayList<PotionEffect>();

        Iterator<PotionEffect> ply_effect_iterator = ply_effects.iterator();

        // loop over players current effects
        while(ply_effect_iterator.hasNext()){
            PotionEffect cur = ply_effect_iterator.next();

            int duration = cur.getDuration() - duration_penalty;
            if(change_duration){
                duration = Math.min(cur.getDuration() - duration_penalty, max_duration);
            }

            int amp = Math.max(cur.getAmplifier() - reduce_potency, 0);

            // if kept
            if(kept_effects.contains(cur.getType())){
                reapplied_effect_list.add(new PotionEffect(cur.getType(), duration, amp, cur.isAmbient(), cur.hasParticles()));
            }
        }

        plugin.potionMap.put(ply.getUniqueId(), reapplied_effect_list);

    }

}
