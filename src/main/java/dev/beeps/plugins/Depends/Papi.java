package dev.beeps.plugins.Depends;

import dev.beeps.plugins.BetterConfig;
import dev.beeps.plugins.BetterKeepInventory;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class Papi extends PlaceholderExpansion {



    @Override
    public @NotNull String getIdentifier() {
        return "BetterKeepInventory";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Beepsterr";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(org.bukkit.entity.Player player, @NotNull String identifier) {

        identifier = identifier.toLowerCase();

        // Seconds left on grace
        if (identifier.equals("grace_timer_seconds")) {
            return String.valueOf(BetterKeepInventory.instance.graceMap.get(player.getUniqueId()));
        }

        // If grace is active
        if(identifier.equals("grace_timer_active")) {
            return BetterKeepInventory.instance.graceMap.get(player.getUniqueId()) != null ? "true" : "false";
        }

        // Overrides!
        for(BetterConfig.OverrideTypes key : BetterConfig.OverrideTypes.values()) {
            if(identifier.equals("is_active_" + key.toString().toLowerCase())) {
                if(BetterKeepInventory.instance.config.GetOverrideForMode("ALL", player)){
                    return "true";
                }
                return BetterKeepInventory.instance.config.GetOverrideForMode(key.toString().toUpperCase(), player) ? "true" : "false";
            }
        }

        return null;
    }
}
