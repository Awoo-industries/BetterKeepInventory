package com.beepsterr.betterkeepinventory.Registries;

import com.beepsterr.betterkeepinventory.api.Effect;
import com.beepsterr.betterkeepinventory.api.Factory.EffectFactory;
import com.beepsterr.betterkeepinventory.api.Registries.EffectRegistry;
import com.beepsterr.betterkeepinventory.api.RegistryEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginEffectRegistry implements EffectRegistry {

    private final Map<String, RegistryEntry<EffectFactory>> factories = new HashMap<>();

    @Override
    public void register(Plugin plugin, String key, EffectFactory factory) {

        RegistryEntry<EffectFactory> entry = new RegistryEntry<>(plugin, factory);

        String baseKey = key.toLowerCase();
        String namespacedKey = (plugin.getName() + "." + key).toLowerCase();

        if(!has(baseKey)){
            factories.put(baseKey, entry);
        }

        factories.put(namespacedKey, entry);
    }

    @Override
    public boolean has(String key) {
        return factories.containsKey(key.toLowerCase());
    }

    @Override
    public EffectFactory get(String key) {
        return this.getFull(key).entry();
    }

    @Override
    public RegistryEntry<EffectFactory> getFull(String key) {
        return factories.get(key.toLowerCase());
    }

    @Override
    public Map<String, RegistryEntry<EffectFactory>> getAll() {
        return Collections.unmodifiableMap(factories);
    }

    @Override
    public Effect create(String key, ConfigurationSection config) {
        RegistryEntry<EffectFactory> factory = factories.get(key.toLowerCase());
        if (factory == null) return null;
        return factory.entry().create(config);
    }
}
