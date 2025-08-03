package com.beepsterr.plugins.Library;

import com.beepsterr.plugins.BetterKeepInventory;
import com.beepsterr.plugins.Effects.*;
import com.beepsterr.plugins.Effects.Configs.*;
import com.beepsterr.plugins.Exceptions.ConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigRule {

    // config properties
    private String name;
    private ConfigRule parent;
    private boolean enabled;
    private ConditionList conditionList;

    // effect runners
    private DamageItemsConfig damage;
    private DropItemsConfig drop;
    private ExpConfig exp;
    private HungerConfig hunger;
    private EconomyConfig economy;

    // Children rules that will need to be triggered if this rule passes checks
    private List<ConfigRule> children;

    public ConfigRule(ConfigurationSection config, ConfigRule parent) throws ConfigurationException {

        this.parent = parent;

        BetterKeepInventory.getInstance().getLogger().info("Parsing RulesEntry for " + config.getCurrentPath());

        // Parse a single rule using the FileConfiguration object and key offset
        this.name = config.getString("name", "Unnamed Rule");
        this.enabled = config.getBoolean("enabled", false);

        // Parse conditions configuration
        if (config.isConfigurationSection("conditions")) {
            this.conditionList = new ConditionList(Objects.requireNonNull(config.getConfigurationSection("conditions")));
        }

        if(config.isConfigurationSection("effects")){
            ConfigurationSection effects = config.getConfigurationSection("effects");
            if(effects != null){
                // Parse damage configuration
                if (effects.isConfigurationSection( "damage")) {
                    this.damage = new DamageItemsConfig(Objects.requireNonNull(effects.getConfigurationSection("damage")));
                }

                // Parse drop configuration
                if (effects.isConfigurationSection( "drop")) {
                    this.drop = new DropItemsConfig(Objects.requireNonNull(effects.getConfigurationSection("drop")));
                }

                // Parse exp configuration
                if (effects.isConfigurationSection( "exp")) {
                    this.exp = new ExpConfig(Objects.requireNonNull(effects.getConfigurationSection("exp")));
                }

                // Parse hunger configuration
                if (effects.isConfigurationSection( "hunger")) {
                    this.hunger = new HungerConfig(Objects.requireNonNull(effects.getConfigurationSection("hunger")));
                }

                // Parse economy configuration
                if (effects.isConfigurationSection( "economy")) {
                    this.economy = new EconomyConfig(Objects.requireNonNull(effects.getConfigurationSection("economy")));
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
                                    ),
                                    this
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

    public ConditionList getConditions() {
        return conditionList;
    }

    public void trigger(Player ply, PlayerDeathEvent deathEvent, PlayerRespawnEvent respawnEvent){

        BetterKeepInventory plugin = BetterKeepInventory.getInstance();

        if(enabled){
            if(conditionList == null || conditionList.check(ply, deathEvent)){
                plugin.debug(ply, "Rule " + this + " met conditions, running effects!");
                if(deathEvent != null){
                    runDeath(ply, deathEvent);
                }
                if(respawnEvent != null){
                    runRespawn(ply, respawnEvent);
                }
            }else{
                plugin.debug(ply, "Rule " + this + " was skipped (conditions not met)");
            }

            for(ConfigRule child : children){
                child.trigger(ply, deathEvent, respawnEvent);
            }

        }else{
            plugin.debug(ply, "Rule " + this + " was skipped (not enabled)");
        }

    }

    private void runDeath(Player ply, PlayerDeathEvent event){
        if(this.damage != null){
            new DamageItemEffect(this, this.damage).runDeath(ply, event);
        }
        if(this.drop != null){
            new DropItemEffect(this, this.drop).runDeath(ply, event);
        }
        if(this.exp != null){
            new ExpEffect(this, this.exp).runDeath(ply, event);
        }
        if(this.hunger != null){
            new HungerEffect(this, this.hunger).runDeath(ply, event);
        }
        if(this.economy != null){
            new EconomyEffect(this, this.economy).runDeath(ply, event);
        }
    }

    private void runRespawn(Player ply, PlayerRespawnEvent event){
        if(this.damage != null){
            new DamageItemEffect(this, this.damage).runRespawn(ply, event);
        }
        if(this.drop != null){
            new DropItemEffect(this, this.drop).runRespawn(ply, event);
        }
        if(this.exp != null){
            new ExpEffect(this, this.exp).runRespawn(ply, event);
        }
        if(this.hunger != null){
            new HungerEffect(this, this.hunger).runRespawn(ply, event);
        }
        if(this.economy != null){
            new EconomyEffect(this, this.economy).runRespawn(ply, event);
        }
    }

    @Override
    public String toString() {
        if(this.parent != null){
            return "ConfigRule{" + parent.getName() + " > " + name + "}";
        }else{
            return "ConfigRule{" + name + "}";
        }
    }
}
