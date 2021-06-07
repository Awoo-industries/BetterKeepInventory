package dev.beeps.plugins;

/*
    COPYRIGHT (c) 2021 Jill "BeepSterr" Megens
    @author BeepSterr
 */

import dev.beeps.plugins.Commands.CmdMain;
import dev.beeps.plugins.Events.PlayerDeath;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;

public final class BetterKeepInventory extends JavaPlugin implements Listener {

    public FileConfiguration config = getConfig();

    @Override
    public void onEnable() {

        initConfig();
        initEvents();
        initCommands();
        initMetrics();

    }

    public void initConfig(){

        config = getConfig();

        config.options().header("#Config for BetterKeepInventory.\n#Read the plugin description to know what each option does: https://www.spigotmc.org/resources/betterkeepinventory.93081/");
        config.addDefault("enabled", true);
        config.addDefault("ignore_enchants", false);
        config.addDefault("dont_break_items", false);
        config.addDefault("min_damage_pct", 25);
        config.addDefault("max_damage_pct", 35);
        config.options().copyDefaults(true);
        saveConfig();

    }

    public void initEvents(){
        getServer().getPluginManager().registerEvents(new PlayerDeath(this), this);
    }

    public void initCommands(){
        Objects.requireNonNull(this.getCommand("betterkeepinventory")).setExecutor(new CmdMain(this));
    }

    public void initMetrics(){
        getServer().getPluginManager().registerEvents(this, this);
        Metrics metrics = new Metrics(this, 11596);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }

}
