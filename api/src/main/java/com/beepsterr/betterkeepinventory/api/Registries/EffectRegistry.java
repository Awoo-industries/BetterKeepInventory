package com.beepsterr.betterkeepinventory.api.Registries;

import com.beepsterr.betterkeepinventory.api.Effect;
import com.beepsterr.betterkeepinventory.api.Factory.EffectFactory;
import com.beepsterr.betterkeepinventory.api.Registry;
import org.bukkit.configuration.ConfigurationSection;

public interface EffectRegistry extends Registry<EffectFactory> {
    Effect create(String key, ConfigurationSection config);
}
