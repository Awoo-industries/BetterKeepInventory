package com.beepsterr.betterkeepinventory.api;

import com.beepsterr.betterkeepinventory.api.Registries.ConditionRegistry;
import com.beepsterr.betterkeepinventory.api.Registries.EffectRegistry;

public record BetterKeepInventoryAPIImpl(
        ConditionRegistry conditionRegistry,
        EffectRegistry effectRegistry
) implements BetterKeepInventoryAPI {}