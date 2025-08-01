package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Effects.DamageItemEffect;
import com.beepsterr.plugins.Effects.DropItemEffect;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigRule {

    // config properties
    private String name;
    private boolean enabled;
    private Conditions conditions;

    // effect runners
    private DamageItems damage;
    private DropItems drop;

    // Children rules that will need to be triggered if this rule passes checks
    private List<ConfigRule> children;

    public ConfigRule(ConfigurationSection config) throws ConfigurationException {

        BetterKeepInventory.getInstance().getLogger().info("Parsing RulesEntry for " + config.getCurrentPath());

        // Parse a single rule using the FileConfiguration object and key offset
        this.name = config.getString("name", "Unnamed Rule");
        this.enabled = config.getBoolean("enabled", false);

        // Parse conditions configuration

        if (config.isConfigurationSection("conditions")) {
            this.conditions = new Conditions(Objects.requireNonNull(config.getConfigurationSection("conditions")));
        }

        if(config.isConfigurationSection("effects")){
            ConfigurationSection effects = config.getConfigurationSection("effects");
            if(effects != null){
                // Parse damage configuration
                if (effects.isConfigurationSection( "damage")) {
                    this.damage = new DamageItems(Objects.requireNonNull(effects.getConfigurationSection("damage")));
                }

                // Parse drop configuration
                if (effects.isConfigurationSection( "drop")) {
                    this.drop = new DropItems(Objects.requireNonNull(effects.getConfigurationSection("drop")));
                }
            }
        }


        // Parse children rules
        this.children = new ArrayList<>();
        if (config.isConfigurationSection( "children")) {
            // make sure children
            ConfigurationSection children = config.getConfigurationSection("children");
            if( children != null ) {
                for (String childKey : children.getKeys(false)) {
                    this.children.add(
                            new ConfigRule(
                                    Objects.requireNonNull(
                                            children.getConfigurationSection(childKey)
                                    )
                            )
                    );
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public void trigger(Player ply, PlayerDeathEvent event){
        if(enabled){
            if(conditions == null || conditions.check(ply, event)){
                BetterKeepInventory.getInstance().getLogger().info("Rule " + name + " met conditions, running effects!");
                runDeath(ply, event);
            }else{
                BetterKeepInventory.getInstance().getLogger().info("Rule " + name + " did not meet conditions, skipping");
            }

            for(ConfigRule child : children){
                BetterKeepInventory.getInstance().getLogger().info("Triggering child rule " + name + "::" + child.getName());
                BetterKeepInventory.getInstance().getLogger().info(child.toString());
                child.trigger(ply, event);
            }
        }else{
            BetterKeepInventory.getInstance().getLogger().info("Rule " + name + " is disabled, skipping");
        }

    }

    private void runDeath(Player ply, PlayerDeathEvent event){
        if(this.damage != null){
            new DamageItemEffect(this, this.damage).runDeath(ply, event);
        }
        if(this.drop != null){
            new DropItemEffect(this, this.drop).runDeath(ply, event);
        }
    }
}
