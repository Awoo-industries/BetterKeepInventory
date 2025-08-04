package com.beepsterr.betterkeepinventory.Registries;

import com.beepsterr.betterkeepinventory.api.Factory.ConditionFactory;
import com.beepsterr.betterkeepinventory.api.Registries.ConditionRegistry;
import com.beepsterr.betterkeepinventory.api.RegistryEntry;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginConditionRegistry implements ConditionRegistry {
    private final Map<String, RegistryEntry<ConditionFactory>> conditions = new HashMap<>();

    @Override
    public void register(Plugin plugin, String key, ConditionFactory condition) {

        RegistryEntry<ConditionFactory> entry = new RegistryEntry<>(plugin, condition);

        String baseKey = key.toLowerCase();
        String namespacedKey = (plugin.getName() + "." + key).toLowerCase();

        if(!has(baseKey)){
            conditions.put(baseKey, entry);
        }

        conditions.put(namespacedKey, entry);
    }

    @Override
    public boolean has(String key) {
        return conditions.containsKey(key.toLowerCase());
    }

    @Override
    public ConditionFactory get(String key) {
        return this.getFull(key).entry();
    }

    @Override
    public RegistryEntry<ConditionFactory> getFull(String key) {
        return conditions.get(key.toLowerCase());
    }

    @Override
    public Map<String, RegistryEntry<ConditionFactory>> getAll() {
        return Collections.unmodifiableMap(conditions);
    }
}
