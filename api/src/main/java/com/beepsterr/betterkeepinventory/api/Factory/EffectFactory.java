package com.beepsterr.betterkeepinventory.api.Factory;

import com.beepsterr.betterkeepinventory.api.Effect;
import org.bukkit.configuration.ConfigurationSection;

@FunctionalInterface
public interface EffectFactory {
    Effect create(ConfigurationSection config);
}