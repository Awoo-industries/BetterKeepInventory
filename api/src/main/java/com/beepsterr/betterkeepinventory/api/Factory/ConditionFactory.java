package com.beepsterr.betterkeepinventory.api.Factory;

import com.beepsterr.betterkeepinventory.api.Condition;
import org.bukkit.configuration.ConfigurationSection;

@FunctionalInterface
public interface ConditionFactory {
    Condition create(ConfigurationSection config);
}