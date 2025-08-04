package com.beepsterr.betterkeepinventory.api;

import com.beepsterr.betterkeepinventory.api.Registries.ConditionRegistry;
import com.beepsterr.betterkeepinventory.api.Registries.EffectRegistry;

public interface BetterKeepInventoryAPI {

    /**
     * Access the condition registry to register or retrieve custom conditions.
     */
    ConditionRegistry conditionRegistry();

    /**
     * Access the effect registry to register or retrieve custom effects.
     */
    EffectRegistry effectRegistry();

}
