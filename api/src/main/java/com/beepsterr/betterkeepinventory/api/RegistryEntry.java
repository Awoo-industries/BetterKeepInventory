package com.beepsterr.betterkeepinventory.api;

import org.bukkit.plugin.Plugin;

public record RegistryEntry<T>(Plugin plugin, T entry) {}

