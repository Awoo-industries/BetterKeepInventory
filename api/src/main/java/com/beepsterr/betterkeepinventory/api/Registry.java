package com.beepsterr.betterkeepinventory.api;

import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface Registry<T> {
    void register(Plugin plugin, String key, T entry);
    boolean has(String key);
    T get(String key);
    RegistryEntry<T> getFull(String key);
    Map<String, RegistryEntry<T>> getAll(); // wrap entries with plugin info
}


